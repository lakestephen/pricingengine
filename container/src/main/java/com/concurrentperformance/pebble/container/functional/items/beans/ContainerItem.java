package com.concurrentperformance.pebble.container.functional.items.beans;

import com.concurrentperformance.pebble.container.functional.output.OutputEventProcessor;
import com.concurrentperformance.pebble.msgcommon.event.Event;
import com.concurrentperformance.pebble.msgcommon.event.IntEvent;
import com.concurrentperformance.pebble.msgcommon.graph.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.List;

public class ContainerItem implements OutputEventHandler { //TODO rename CalculableItem

    private final Log log = LogFactory.getLog(this.getClass());

    private final String path;
    private final GraphCalculation calculation;

    // Input stuff
    private String[] inputEventIds;
    private Event[] inputEvents;
    private volatile boolean dirtyInputs;

    // Output Stuff
    private String outputEventId;
    private Event outputEventCache;
    private boolean hubInterest = false; // TODO should this be volatile?
    private boolean monitorInterest = true; // TODO should this be volatile?  //TODO need to express interest.
    private OutputEventProcessor outputEventProcessor;

    // General State
    private ContainerItemState state = ContainerItemState.INITIALISATION;//TODO should be volatile?

    public ContainerItem(String path, GraphCalculation calculation) {
        this.path = path;
        this.calculation = calculation;
    }

    public void setInputDetails(List<String> inputEventIds) {
        this.inputEventIds = inputEventIds.toArray(new String[inputEventIds.size()]);
        // as we are an input, configure the inputs
        this.inputEvents = new Event[((GraphCalculationInput) calculation).getInputEventDefinition().length];
    }

    public void setOutputDetails(OutputEventProcessor outputEventProcessor, String outputEventId) {
        this.outputEventProcessor = outputEventProcessor;
        this.outputEventId = outputEventId;
        ((GraphCalculationOutput) calculation).setOutputEventHandler(this);
        ((GraphCalculationOutput) calculation).setOutputEventId(outputEventId);
    }

    /**
     * Set the value that was persisted to the database last time, and create
     * a new outputEventCache.
     *
     * @param value
     */
    public void setValueArtificially(int value) {
        outputEventCache = new IntEvent(outputEventId, value);
    }

    public void updateInputEvent(Event event) {
        String id = event.getId();
        for (int i = 0; i < inputEventIds.length; i++) { //TODO how to do this more efficiently
            if (id.equals(inputEventIds[i])) {
                inputEvents[i] = event;
                dirtyInputs = true;
                //remember that one input might go to two places
            }
        }
    }

    public boolean isDirtyInputs() { //TODO needs to be a CAS operation that sets clean.
        return dirtyInputs;
    }

    public void calculate() {
        GraphCalculationInputDefinition[] inputs = ((GraphCalculationInput) calculation).getInputEventDefinition();

        boolean allMandatoryInputsValid = true;

        for (int i = 0; i < inputs.length; i++) { // TODO could speed up here if only the ones that are changed are re-checked.
            if (inputs[i].isMandatory() && inputEvents[i] == null) {
                allMandatoryInputsValid = false;
                state = ContainerItemState.INVALID_INPUTS;
                break;
            }
        }

        if (allMandatoryInputsValid) {
            state = ContainerItemState.CALCULATING;
            ((GraphCalculationInput) calculation).calculate(inputEvents);
        }
        //log.info("Calculate ContainerItem [" + path + "], state = [" + state + "]");
    }

    @Override
    public void acceptOutputEvent(Event outputEvent) {
        outputEventCache = outputEvent;
        outputEventProcessor.outputEvent(outputEventCache, hubInterest, monitorInterest);
    }

    public void start() {
        if (calculation instanceof GraphCalculationOutput) {
            ((GraphCalculationOutput) calculation).start();
        }
        // update the world of our value if one was created on startup
        // from a stored database value
        if (outputEventCache != null) {
            // this passes false for both hub and monitor interest, as
            // on hub or monitor registering an interest, it will write the
            // current value back.
            outputEventProcessor.outputEvent(outputEventCache, false, false);
        }
    }

    public void setHubInterest(boolean hubInterest) {
        this.hubInterest = hubInterest;
        if (outputEventCache != null) {
            outputEventProcessor.outputEvent(outputEventCache, hubInterest, false);
        }
    }

    public void setMonitorInterest(boolean monitorInterest) {
        this.monitorInterest = monitorInterest;
        if (outputEventCache != null) {
            outputEventProcessor.outputEvent(outputEventCache, false, monitorInterest);
        }
    }

    @Override
    public String toString() {
        return "[" + calculation.getCalculationName() + "@" + path + "], [" + Arrays.toString(inputEvents) + "]";
    }
}
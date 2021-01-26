package com.concurrentperformance.pebble.container.functional.graph.dao.impl;

import com.concurrentperformance.pebble.container.functional.graph.beans.GraphItem;
import com.concurrentperformance.pebble.container.functional.graph.beans.GraphItemValueInt;
import com.concurrentperformance.pebble.container.functional.graph.beans.SimpleMountItem;
import com.concurrentperformance.pebble.container.functional.graph.dao.GraphDao;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HibernateGraphDao extends HibernateDaoSupport implements GraphDao {

    @Override
    public void persistGraphItem(GraphItem graphItem) {
        getHibernateTemplate().saveOrUpdate(graphItem);
    }

    @Override
    public void persistGraphValue(GraphItemValueInt value) {
        getHibernateTemplate().saveOrUpdate(value);
    }


    @Override
    public Set<Long> getMountIdsForContainerId(long hostContainerId) {
        @SuppressWarnings("unchecked")
        List<SimpleMountItem> list = getHibernateTemplate().find("from SimpleMountItem mountItem where hostContainerId = ?", hostContainerId);

        Set<Long> mountIds = new HashSet<Long>();
        for (SimpleMountItem simpleMountItem : list) {
            mountIds.add(simpleMountItem.getId());
        }

        return mountIds;
    }

    @Override
    public Set<GraphItem> getAllGraphItemsForMount(long mountId) {
        Set<GraphItem> graphItems = null;

        @SuppressWarnings("unchecked")
        List<GraphItem> list = getHibernateTemplate().find("from GraphItem graphItem where mountId = ?", mountId);

        graphItems = new HashSet<GraphItem>(list);

        return graphItems;
    }

    @Override
    public GraphItem findGraphItem(long id) {
        GraphItem graphItem = null;

        @SuppressWarnings("unchecked")
        List<GraphItem> list = getHibernateTemplate().find("from GraphItem graphItem where id = ?", id);

        if (list != null && list.size() > 0) {
            graphItem = list.get(0);
        }

        return graphItem;
    }

    @Override
    public Set<GraphItemValueInt> getAllGraphValueInt() {
        Set<GraphItemValueInt> graphItemValueInts = null;

        @SuppressWarnings("unchecked")
        List<GraphItemValueInt> list = getHibernateTemplate().find("from GraphItemValueInt graphItemValueInt");

        graphItemValueInts = new HashSet<GraphItemValueInt>(list);

        return graphItemValueInts;
    }

    @Override
    public GraphItemValueInt getGraphValueInt(long graphitemId) {
        GraphItemValueInt graphItemValueInt = null;

        @SuppressWarnings("unchecked")
        List<GraphItemValueInt> list = getHibernateTemplate().find("from GraphItemValueInt graphItemValueInt where graphitemId = ?", graphitemId);

        if (list != null && list.size() > 0) {
            graphItemValueInt = list.get(0);
        }

        return graphItemValueInt;
    }


}
select id, name, parent_machine_id, parent_hub_id, host_container_id from topology t
left join topology_hub th on th.topology_id = t.id
left join topology_container tc on tc.topology_id = t.id
left join topology_mount tm on tm.topology_id = t.id
order by id
go

select * from graph_item gi
left join graph_int_value giv on giv.graphitemid = id

go
select * from graph_item_input

go

delete from graph_item_input;
delete from graph_int_value;
delete from graph_item ;

delete from topology_mount ;
delete from topology_container  ;
delete from topology_hub ;
delete from topology_machine  ;
delete from topology ;

select * from topology;
select * from graph_item;
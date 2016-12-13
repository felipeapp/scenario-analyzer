select * from node_scenario inner join node on node_scenario.node_id = node.id
where node.member like '%Server.doStart%' and node_scenario.scenario_id = 120

select * from scenario where name like 'Entry point for DefaultServletTest.testFiltered%'

select * from node where id = 121509
select * from node where id = 121512

select * from scenario where root_id = 121014

select node.* from scenario, node, node_scenario
where scenario.id = node_scenario.scenario_id and node.id = node_scenario.node_id and
scenario.name like 'Entry point for DefaultServletTest.testFiltered%'
--node.member like '%Server.doStart%'


select avg(node.time) from scenario inner join node on scenario.root_id = node.id and
scenario.name = 'Entry point for DefaultServletTest.testFiltered' and node.time <> -1-- order by node.member

select avg(node.time) from node inner join node_scenario on node.id = node_scenario.node_id
inner join scenario on node_scenario.scenario_id = scenario.id
where node.time <> -1 and scenario.name = 'Entry point for DefaultServletTest.testFiltered' and
node.member = 'org.eclipse.jetty.servlet.DefaultServletTest.testFiltered()'

select avg(node.time) from node inner join node_scenario on node.id = node_scenario.node_id
inner join scenario on node_scenario.scenario_id = scenario.id
where node.time <> -1 and scenario.name = 'Entry point for DefaultServletTest.testFiltered' and
node.member like '%Server.doStart%'

select node.* from node inner join node_scenario on node.id = node_scenario.node_id
inner join scenario on node_scenario.scenario_id = scenario.id
where node.time <> -1 and scenario.name = 'Entry point for DefaultServletTest.testFiltered' and
node.member like '%AbstractLifeCycle.start%' and node.id in (

select node.parent_id from node inner join node_scenario on node.id = node_scenario.node_id
inner join scenario on node_scenario.scenario_id = scenario.id
where node.time <> -1 and scenario.name = 'Entry point for DefaultServletTest.testFiltered' and
node.member like '%Server.doStart%'

) order by node.parent_id




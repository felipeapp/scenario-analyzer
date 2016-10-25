ALTER TABLE node_scenario ADD CONSTRAINT node_scenario_pkey PRIMARY KEY(scenario_id, node_id);
CREATE INDEX name_idx ON scenario(name);
CREATE INDEX member_idx ON node(member);
CREATE INDEX time_idx ON node(time);

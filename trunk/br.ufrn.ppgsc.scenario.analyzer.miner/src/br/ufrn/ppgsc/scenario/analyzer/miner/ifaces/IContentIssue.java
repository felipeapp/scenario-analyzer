package br.ufrn.ppgsc.scenario.analyzer.miner.ifaces;

public interface IContentIssue {

	public long getId();

	public void setId(long id);

	public long getNumber();

	public void setNumber(long number);

	public long getIdType();

	public void setIdType(long idType);

	public String getTypeName();

	public void setTypeName(String typeName);

}

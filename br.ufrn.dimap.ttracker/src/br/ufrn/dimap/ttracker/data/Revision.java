package br.ufrn.dimap.ttracker.data;

import java.io.Serializable;

public class Revision implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Task task;
	private Float inclusion;
	private Float precision;
	private Float MC;
	private Float MnC;
	private Float nMC;
	private Float nMnC;
	private Revision oldRevision;
	
	public Revision() {
	}
	
	public Revision(Integer id, Task task) {
		this.task = task;
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Float getInclusion() {
		return inclusion;
	}

	public void setInclusion(Float inclusion) {
		this.inclusion = inclusion;
	}

	public Float getPrecision() {
		return precision;
	}

	public void setPrecision(Float precision) {
		this.precision = precision;
	}

	public Float getMC() {
		return MC;
	}

	public void setMC(Float mC) {
		MC = mC;
	}

	public Float getMnC() {
		return MnC;
	}

	public void setMnC(Float mnC) {
		MnC = mnC;
	}

	public Float getnMC() {
		return nMC;
	}

	public void setnMC(Float nMC) {
		this.nMC = nMC;
	}

	public Float getnMnC() {
		return nMnC;
	}

	public void setnMnC(Float nMnC) {
		this.nMnC = nMnC;
	}

	public Revision getOldRevision() {
		return oldRevision;
	}

	public void setOldRevision(Revision oldRevision) {
		this.oldRevision = oldRevision;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Revision other = (Revision) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}

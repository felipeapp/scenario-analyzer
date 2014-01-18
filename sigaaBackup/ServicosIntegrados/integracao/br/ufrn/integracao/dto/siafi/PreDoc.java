package br.ufrn.integracao.dto.siafi;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("preDoc")
public class PreDoc {
	
	@XStreamAlias("predocOB")
	private PredocOB predocOB;//Obrigatório
	
	@XStreamAlias("predocNS")
	private PredocNS predocNS;//Obrigatório
	
	@XStreamAlias("predocDARF")
	private PredocDARF predocDARF;//Obrigatório
	
	@XStreamAlias("predocDAR")
	private PredocDAR predocDAR;//Obrigatório
	
	@XStreamAlias("predocGRU")
	private PredocGRU predocGRU;//Obrigatório
	
	@XStreamAlias("predocGPS")
	private PredocGPS predocGPS;//Obrigatório
	
	@XStreamAlias("predocGFIP")
	private PredocGFIP predocGFIP;//Obrigatório
	
	@XStreamAlias("predocPF")
	private PredocPF predocPF;//Obrigatório
	
	@XStreamAlias("txtObser")
	private String txtObser;//Obrigatório

	public PredocOB getPredocOB() {
		return predocOB;
	}

	public void setPredocOB(PredocOB predocOB) {
		this.predocOB = predocOB;
	}

	public PredocNS getPredocNS() {
		return predocNS;
	}

	public void setPredocNS(PredocNS predocNS) {
		this.predocNS = predocNS;
	}

	public PredocDARF getPredocDARF() {
		return predocDARF;
	}

	public void setPredocDARF(PredocDARF predocDARF) {
		this.predocDARF = predocDARF;
	}

	public PredocDAR getPredocDAR() {
		return predocDAR;
	}

	public void setPredocDAR(PredocDAR predocDAR) {
		this.predocDAR = predocDAR;
	}

	public PredocGRU getPredocGRU() {
		return predocGRU;
	}

	public void setPredocGRU(PredocGRU predocGRU) {
		this.predocGRU = predocGRU;
	}

	public PredocGPS getPredocGPS() {
		return predocGPS;
	}

	public void setPredocGPS(PredocGPS predocGPS) {
		this.predocGPS = predocGPS;
	}

	public PredocGFIP getPredocGFIP() {
		return predocGFIP;
	}

	public void setPredocGFIP(PredocGFIP predocGFIP) {
		this.predocGFIP = predocGFIP;
	}

	public PredocPF getPredocPF() {
		return predocPF;
	}

	public void setPredocPF(PredocPF predocPF) {
		this.predocPF = predocPF;
	}

	public String getTxtObser() {
		return txtObser;
	}

	public void setTxtObser(String txtObser) {
		this.txtObser = txtObser;
	}
}

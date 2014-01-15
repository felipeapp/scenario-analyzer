/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 09/01/2009
 *
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe auxiliar que guarda os dados das consultas para exibir os dados nos relatórios da biblioteca.
 * @author Fred_Castro
 *
 */

public class RelatorioBiblioteca {
	
	/** Atributos para auxiliar na geração de diversos relatórios da biblioteca,
	 * estes atributos podem serem usados diferentemente para cada relatório.
	 * Guardando os dados conforme a necessidade. */
	private Integer tld, vld, tdvd, vdvd, tpartitura, vpartitura,
				   tvideo, vvideo, tcd, vcd, tcdrom, vcdrom, tvinil,
				   vvinil, tdisquete, vdisquete, tfotografia, vfotografia,
				   tcassete, vcassete, tmicroficha, vmicroficha, tmapa, vmapa,
				   tslide, vslide, ttotal, vtotal, t0, v0, t1, v1, t2, v2, t3, v3,
				   t4, v4, t5, v5, t6, v6, t7, v7, t8, v8, t9, v9;
	
	private boolean total;

	private Integer tnc, tic, tnn, tin, vnc, vic, vnn, vin;
	
	private String nome;
	
	private Map <String, Integer> areasCnpqE = new HashMap <String, Integer> ();
	private Map <String, Integer> areasCnpqT = new HashMap <String, Integer> ();
	private Map <String, Integer> areasCnpqF =new HashMap<String, Integer> ();
	
	public RelatorioBiblioteca () {
		// Vazio
	}
	
	public RelatorioBiblioteca (String nome, Integer tld, Integer vld, Integer tdvd, Integer vdvd, Integer tpartitura, Integer vpartitura, Integer tvideo, Integer vvideo, Integer tcd, Integer vcd, Integer tcdrom, Integer vcdrom, Integer tvinil, Integer vvinil, Integer tdisquete, Integer vdisquete, Integer tfotografia, Integer vfotografia, Integer tcassete, Integer vcassete, Integer tmicroficha, Integer vmicroficha, Integer tmapa, Integer vmapa, Integer tslide, Integer vslide, Integer ttotal, Integer vtotal) {
		this.nome = nome;
		this.tcassete = tcassete;
		this.tcd = tcd;
		this.tcdrom = tcdrom;
		this.tdisquete = tdisquete;
		this.tdvd = tdvd;
		this.tfotografia = tfotografia;
		this.tld = tld;
		this.tmapa = tmapa;
		this.tmicroficha = tmicroficha;
		this.tpartitura = tpartitura;
		this.tslide = tslide;
		this.ttotal = ttotal;
		this.tvideo = tvideo;
		this.tvinil = tvinil;
		this.vcassete = vcassete;
		this.vcd = vcd;
		this.vcdrom = vcdrom;
		this.vdisquete = vdisquete;
		this.vdvd = vdvd;
		this.vfotografia = vfotografia;
		this.vld = vld;
		this.vmapa = vmapa;
		this.vmicroficha = vmicroficha;
		this.vpartitura = vpartitura;
		this.vslide = vslide;
		this.vtotal = vtotal;
		this.vvideo = vvideo;
		this.vvinil = vvinil;
	}
	
	public RelatorioBiblioteca (String nome, Integer vnc, Integer tnc,
			Integer vic, Integer tic, Integer vnn, Integer tnn, Integer vin,
			Integer tin, Integer vtotal, Integer ttotal) {
		this.nome = nome;
		this.tic = tic;
		this.tin = tin;
		this.tnc = tnc;
		this.tnn = tnn;
		this.ttotal = ttotal;
		this.vic = vic;
		this.vin = vin;
		this.vnc = vnc;
		this.vnn = vnn;
		this.vtotal = vtotal;
	}
	
	public RelatorioBiblioteca (String nome, Map <String, Integer> areasCnpqE, Map <String, Integer> areasCnpqT){
		this.nome = nome;
		this.areasCnpqE = areasCnpqE;
		this.areasCnpqT = areasCnpqT;
	}
	
	public RelatorioBiblioteca(String nome, Integer vtotal, Integer ttotal){
		this.nome = nome;
		this.ttotal = ttotal;
		this.vtotal = vtotal;
	}

	public RelatorioBiblioteca(String nome, Integer v0, Integer v1, Integer v2,
			Integer v3, Integer v4, Integer v5, Integer v6, Integer v7,
			Integer v8, Integer v9, Integer vtotal, Integer t0, Integer t1,
			Integer t2, Integer t3, Integer t4, Integer t5, Integer t6,
			Integer t7, Integer t8, Integer t9, Integer ttotal) {
		super();
		this.nome = nome;
		this.t0 = t0;
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
		this.t4 = t4;
		this.t5 = t5;
		this.t6 = t6;
		this.t7 = t7;
		this.t8 = t8;
		this.t9 = t9;
		this.ttotal = ttotal;
		this.v0 = v0;
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.v4 = v4;
		this.v5 = v5;
		this.v6 = v6;
		this.v7 = v7;
		this.v8 = v8;
		this.v9 = v9;
		this.vtotal = vtotal;
	}
	
	
	public Integer getTtotal() {
		return ttotal;
	}

	public void setTtotal(Integer ttotal) {
		this.ttotal = ttotal;
	}

	public Integer getVtotal() {
		return vtotal;
	}

	public void setVtotal(Integer vtotal) {
		this.vtotal = vtotal;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getTld() {
		return tld;
	}

	public void setTld(Integer tld) {
		this.tld = tld;
	}

	public Integer getVld() {
		return vld;
	}

	public void setVld(Integer vld) {
		this.vld = vld;
	}

	public Integer getTdvd() {
		return tdvd;
	}

	public void setTdvd(Integer tdvd) {
		this.tdvd = tdvd;
	}

	public Integer getVdvd() {
		return vdvd;
	}

	public void setVdvd(Integer vdvd) {
		this.vdvd = vdvd;
	}

	public Integer getTpartitura() {
		return tpartitura;
	}

	public void setTpartitura(Integer tpartitura) {
		this.tpartitura = tpartitura;
	}

	public Integer getVpartitura() {
		return vpartitura;
	}

	public void setVpartitura(Integer vpartitura) {
		this.vpartitura = vpartitura;
	}

	public Integer getTvideo() {
		return tvideo;
	}

	public void setTvideo(Integer tvideo) {
		this.tvideo = tvideo;
	}

	public Integer getVvideo() {
		return vvideo;
	}

	public void setVvideo(Integer vvideo) {
		this.vvideo = vvideo;
	}

	public Integer getTcd() {
		return tcd;
	}

	public void setTcd(Integer tcd) {
		this.tcd = tcd;
	}

	public Integer getVcd() {
		return vcd;
	}

	public void setVcd(Integer vcd) {
		this.vcd = vcd;
	}

	public Integer getTcdrom() {
		return tcdrom;
	}

	public void setTcdrom(Integer tcdrom) {
		this.tcdrom = tcdrom;
	}

	public Integer getVcdrom() {
		return vcdrom;
	}

	public void setVcdrom(Integer vcdrom) {
		this.vcdrom = vcdrom;
	}

	public Integer getTvinil() {
		return tvinil;
	}

	public void setTvinil(Integer tvinil) {
		this.tvinil = tvinil;
	}

	public Integer getVvinil() {
		return vvinil;
	}

	public void setVvinil(Integer vvinil) {
		this.vvinil = vvinil;
	}

	public Integer getTdisquete() {
		return tdisquete;
	}

	public void setTdisquete(Integer tdisquete) {
		this.tdisquete = tdisquete;
	}

	public Integer getVdisquete() {
		return vdisquete;
	}

	public void setVdisquete(Integer vdisquete) {
		this.vdisquete = vdisquete;
	}

	public Integer getTfotografia() {
		return tfotografia;
	}

	public void setTfotografia(Integer tfotografia) {
		this.tfotografia = tfotografia;
	}

	public Integer getVfotografia() {
		return vfotografia;
	}

	public void setVfotografia(Integer vfotografia) {
		this.vfotografia = vfotografia;
	}

	public Integer getTcassete() {
		return tcassete;
	}

	public void setTcassete(Integer tcassete) {
		this.tcassete = tcassete;
	}

	public Integer getVcassete() {
		return vcassete;
	}

	public void setVcassete(Integer vcassete) {
		this.vcassete = vcassete;
	}

	public Integer getTmicroficha() {
		return tmicroficha;
	}

	public void setTmicroficha(Integer tmicroficha) {
		this.tmicroficha = tmicroficha;
	}

	public Integer getVmicroficha() {
		return vmicroficha;
	}

	public void setVmicroficha(Integer vmicroficha) {
		this.vmicroficha = vmicroficha;
	}

	public Integer getTmapa() {
		return tmapa;
	}

	public void setTmapa(Integer tmapa) {
		this.tmapa = tmapa;
	}

	public Integer getVmapa() {
		return vmapa;
	}

	public void setVmapa(Integer vmapa) {
		this.vmapa = vmapa;
	}

	public Integer getTslide() {
		return tslide;
	}

	public void setTslide(Integer tslide) {
		this.tslide = tslide;
	}

	public Integer getVslide() {
		return vslide;
	}

	public void setVslide(Integer vslide) {
		this.vslide = vslide;
	}

	public Integer getT0() {
		return t0;
	}

	public void setT0(Integer t0) {
		this.t0 = t0;
	}

	public Integer getV0() {
		return v0;
	}

	public void setV0(Integer v0) {
		this.v0 = v0;
	}

	public Integer getT1() {
		return t1;
	}

	public void setT1(Integer t1) {
		this.t1 = t1;
	}

	public Integer getV1() {
		return v1;
	}

	public void setV1(Integer v1) {
		this.v1 = v1;
	}

	public Integer getT2() {
		return t2;
	}

	public void setT2(Integer t2) {
		this.t2 = t2;
	}

	public Integer getV2() {
		return v2;
	}

	public void setV2(Integer v2) {
		this.v2 = v2;
	}

	public Integer getT3() {
		return t3;
	}

	public void setT3(Integer t3) {
		this.t3 = t3;
	}

	public Integer getV3() {
		return v3;
	}

	public void setV3(Integer v3) {
		this.v3 = v3;
	}

	public Integer getT4() {
		return t4;
	}

	public void setT4(Integer t4) {
		this.t4 = t4;
	}

	public Integer getV4() {
		return v4;
	}

	public void setV4(Integer v4) {
		this.v4 = v4;
	}

	public Integer getT5() {
		return t5;
	}

	public void setT5(Integer t5) {
		this.t5 = t5;
	}

	public Integer getV5() {
		return v5;
	}

	public void setV5(Integer v5) {
		this.v5 = v5;
	}

	public Integer getT6() {
		return t6;
	}

	public void setT6(Integer t6) {
		this.t6 = t6;
	}

	public Integer getV6() {
		return v6;
	}

	public void setV6(Integer v6) {
		this.v6 = v6;
	}

	public Integer getT7() {
		return t7;
	}

	public void setT7(Integer t7) {
		this.t7 = t7;
	}

	public Integer getV7() {
		return v7;
	}

	public void setV7(Integer v7) {
		this.v7 = v7;
	}

	public Integer getT8() {
		return t8;
	}

	public void setT8(Integer t8) {
		this.t8 = t8;
	}

	public Integer getV8() {
		return v8;
	}

	public void setV8(Integer v8) {
		this.v8 = v8;
	}

	public Integer getT9() {
		return t9;
	}

	public void setT9(Integer t9) {
		this.t9 = t9;
	}

	public Integer getV9() {
		return v9;
	}

	public void setV9(Integer v9) {
		this.v9 = v9;
	}

	public Integer getTnc() {
		return tnc;
	}

	public void setTnc(Integer tnc) {
		this.tnc = tnc;
	}

	public Integer getTic() {
		return tic;
	}

	public void setTic(Integer tic) {
		this.tic = tic;
	}

	public Integer getTnn() {
		return tnn;
	}

	public void setTnn(Integer tnn) {
		this.tnn = tnn;
	}

	public Integer getTin() {
		return tin;
	}

	public void setTin(Integer tin) {
		this.tin = tin;
	}

	public Integer getVnc() {
		return vnc;
	}

	public void setVnc(Integer vnc) {
		this.vnc = vnc;
	}

	public Integer getVic() {
		return vic;
	}

	public void setVic(Integer vic) {
		this.vic = vic;
	}

	public Integer getVnn() {
		return vnn;
	}

	public void setVnn(Integer vnn) {
		this.vnn = vnn;
	}

	public Integer getVin() {
		return vin;
	}

	public void setVin(Integer vin) {
		this.vin = vin;
	}

	public boolean isTotal() {
		return total;
	}

	public void setTotal(boolean total) {
		this.total = total;
	}

	public Map<String, Integer> getAreasCnpqE() {
		return areasCnpqE;
	}

	public void setAreasCnpqE(Map<String, Integer> areasCnpqE) {
		this.areasCnpqE = areasCnpqE;
	}

	public Map<String, Integer> getAreasCnpqT() {
		return areasCnpqT;
	}

	public void setAreasCnpqT(Map<String, Integer> areasCnpqT) {
		this.areasCnpqT = areasCnpqT;
	}
	
	public Map<String, Integer> getAreasCnpqF() {
		return areasCnpqF;
	}

	public void setAreasCnpqF(Map<String, Integer> areasCnpqF) {
		this.areasCnpqF = areasCnpqF;
	}
}

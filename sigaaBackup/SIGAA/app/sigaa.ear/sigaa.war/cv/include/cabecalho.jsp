<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@ taglib prefix="p"uri="/tags/primefaces-p" %>

<jwr:style src="/cv/css/comunidade_virtual.css" media="all"/>

<style>
	.navegacao {
		height:20px;
		width:95%;
		font-size:8pt;
		position:relative;
		margin:10px auto 0px auto;
	}
	
	.navegacao .resultados {
		position:absolute;
		bottom:0px;
		left:0px;
	}
	
	.navegacao .opcoes {
		float:right;
		margin-right:10px;
	}
	
	.navegacao .opcoes span {
		font-weight:bold;
		color:#666666;
	}
	
	.navegacao .setas {
		float:right;
		width:145px;
	}
	
	.navegacao .setas div {
		float:left;
	}
</style>

<h2> Você está na comunidade: ${comunidadeVirtualMBean.comunidade.nome} </h2>


<div id="wrapper">
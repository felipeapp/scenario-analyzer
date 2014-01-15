<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<link rel="stylesheet" type="text/css" href="/shared/javascript/ext-2.0.a.1/resources/css/ext-all.css" />
	
<script src="/shared/javascript/ext-2.0.a.1/ext-base.js"></script>
<script src="/shared/javascript/ext-2.0.a.1/ext-all.js"></script>


<link rel="stylesheet" type="text/css" media="all" href="/sigaa/css/turma-virtual/porta-arquivos.css"/>

<f:view>

<h:form>
	<c:set var="tamanhoOcupado" value="#{ arquivoUsuario.tamanhoTotalOcupado }"/>
	<c:set var="tamanhoTotal" value="#{ arquivoUsuario.tamanhoPortaArquivos }"/>
	<c:set var="porcentagem" value="${ tamanhoOcupado / tamanhoTotal }"/>
	
	<h2><ufrn:subSistema /> &gt; Porta Arquivos</h2>
	
	<script type="text/javascript">
		var objs = new Array();
		${ pastaArquivos.pastasUsuario }
	</script>

	<div id="ajuda-porta-arquivos">
		<p class="bem-vindo">
		Bem vindo ao porta-arquivos do SIGAA. Com esta funcionalidade você poderá guardar os arquivos
		que utiliza nas aulas para disponibilizar para seus alunos.
		Você está usando <strong>
		<c:if test="${ tamanhoOcupado >= (1024 * 1024) }"><fmt:formatNumber value="${ tamanhoOcupado / (1024 * 1024) }" pattern="#0.00"/>MB</c:if>
		<c:if test="${ tamanhoOcupado < (1024 * 1024) }"><fmt:formatNumber value="${ tamanhoOcupado / 1024 }" pattern="#0.00"/>KB</c:if> </strong>
		de
		<strong><c:if test="${ tamanhoTotal >= (1024 * 1024) }"><fmt:formatNumber value="${ tamanhoTotal / (1024 * 1024) }" pattern="#0.00"/>MB</c:if>
		<c:if test="${ tamanhoTotal < (1024 * 1024) }"><fmt:formatNumber value="${ tamanhoTotal / 1024 }" pattern="#0.00"/>KB</c:if></strong>
		disponíveis. Isso corresponde a <strong><fmt:formatNumber value="${ porcentagem * 100 }" pattern="#0.0"/>%</strong> do total.
		</p>
		<div id="barra-uso">
			<div class="usado" style="width: ${ porcentagem * 100 }%">&nbsp;</div>
			<div class="total"><fmt:formatNumber value="${ porcentagem * 100 }" pattern="#0.0"/>%</div>
		</div>
	</div>

	<div id="porta-arquivos">
		<div id="toolbar"></div>
		<div id="arquivos"></div>
		<div id="diretorios"></div>
		<div class="clear"></div>
	</div>
	
	<div id="dummyDiv"></div>
	
	<div id="painel-nova-pasta"></div>
	<div id="painel-remover-pasta"></div>
	<div id="painel-alterar-pasta"></div>
	<div id="painel-renomear"></div>
	
	<input type="hidden" name="idArquivo" id="idArquivo"/>
	<input type="hidden" name="idPasta" id="idPasta"/>
	<input type="hidden" name="pastaAtual" id="pastaAtual" value="-1"/>

</h:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

</f:view>

	
<script type="text/javascript" src="/sigaa/javascript/porta-arquivos/arquivos.js"></script>
<script type="text/javascript" src="/sigaa/javascript/porta-arquivos/diretorios.js"></script>
<script type="text/javascript" src="/sigaa/javascript/porta-arquivos/layout.js"></script> 
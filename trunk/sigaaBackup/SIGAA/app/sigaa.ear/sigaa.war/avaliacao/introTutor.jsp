<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" href="${ctx}/css/avaliacao-institucional.css" type="text/css"/>

<f:view>
<h:form>

<h2><ufrn:subSistema /> > Avalia��o Institucional</h2>

<div class="intro-aval">
	<h4>A avalia��o da doc�ncia agora � On-line!</h4>
	<h5>Contribua para uma ${ configSistema['siglaInstituicao'] } de maior qualidade!</h5>
	<div class="textos">
		<p class="cumprimento">Caro Tutor,</p>
		<p class="texto">A sua avalia��o � utilizada pela Reitoria para definir as a��es de melhorias da Universidade.</p>
		<p class="texto">A ${ configSistema['siglaInstituicao'] } conta com a fidedignidade das suas informa��es, pois elas ser�o o norte da pol�tica universit�ria.</p>
		<p class="assinatura">Atenciosamente,<br />Comiss�o Pr�pria de Avalia��o<br /><a href="mailto:cpa@proplan.ufrn.br">cpa@proplan.ufrn.br</a></p> 
	</div>
	<div class="botao left">
		<c:if test="${ avaliacaoInstitucional.obj.id == 0 }">
		<h:commandLink action="#{ avaliacaoInstitucional.avaliacaoTutor }">
			<f:verbatim><span>Iniciar Avalia��o</span></f:verbatim>
		</h:commandLink>
		</c:if>
		<c:if test="${ avaliacaoInstitucional.obj.id != 0 }">
		<h:commandLink action="#{ avaliacaoInstitucional.avaliacaoTutor }">
			<f:verbatim><span>Continuar Avalia��o</span></f:verbatim>
		</h:commandLink>
		</c:if>
	</div>
	<div class="botao_depois right">
			<a href="${ctx}/verPortalTutor.do"><span>Avaliar Depois</span></a>
	</div>
</div>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

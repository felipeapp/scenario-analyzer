<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" href="${ctx}/css/avaliacao-institucional.css" type="text/css"/>

<f:view>
<h:form>

<h2><ufrn:subSistema /> > Avaliação Institucional</h2>

<div class="intro-aval">
	<h4>A avaliação da docência agora é On-line!</h4>
	<h5>Contribua para uma ${ configSistema['siglaInstituicao'] } de maior qualidade!</h5>
	<div class="textos">
		<p class="cumprimento">Caro Tutor,</p>
		<p class="texto">A sua avaliação é utilizada pela Reitoria para definir as ações de melhorias da Universidade.</p>
		<p class="texto">A ${ configSistema['siglaInstituicao'] } conta com a fidedignidade das suas informações, pois elas serão o norte da política universitária.</p>
		<p class="assinatura">Atenciosamente,<br />Comissão Própria de Avaliação<br /><a href="mailto:cpa@proplan.ufrn.br">cpa@proplan.ufrn.br</a></p> 
	</div>
	<div class="botao left">
		<c:if test="${ avaliacaoInstitucional.obj.id == 0 }">
		<h:commandLink action="#{ avaliacaoInstitucional.avaliacaoTutor }">
			<f:verbatim><span>Iniciar Avaliação</span></f:verbatim>
		</h:commandLink>
		</c:if>
		<c:if test="${ avaliacaoInstitucional.obj.id != 0 }">
		<h:commandLink action="#{ avaliacaoInstitucional.avaliacaoTutor }">
			<f:verbatim><span>Continuar Avaliação</span></f:verbatim>
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

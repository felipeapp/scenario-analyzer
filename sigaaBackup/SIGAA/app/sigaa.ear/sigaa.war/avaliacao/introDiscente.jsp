<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" href="${ctx}/css/avaliacao-institucional.css" type="text/css"/>

<%-- Flowplayer --%>
	<script type="text/javascript" src="${ctx}/avaliacao/flowplayer/flowplayer-3.2.2.min.js"></script>

<%-- Estilo especifico --%>
<style>
	div.intro-aval {
		float: left; width:435px !important;
	}
	
	#conteudo h4 {
		display:block; margin:0 0 27px 100px; position:relative; width:500px;
	}
	
	#conteudo h5 {
		font-size:15px; font-weight:bold; margin-bottom: 5px;
	}
	
	div.bloco_video {
		float: right; width:290px; margin: 75px 0 0; text-align:left;
	}

		div.bloco_video p {
			width:280px; text-align: center; color:#CC3333; font-size:15px; font-weight: bold;
		}
</style>

<f:view>
<h:form>

<h2>Avalia��o Institucional</h2>

<div class="intro-aval">
	<h4>A avalia��o da doc�ncia agora � On-line!</h4>
	<h5>Contribua para uma ${ configSistema['siglaInstituicao'] } de maior qualidade!</h5>
	<div class="textos">
		<p class="cumprimento">Caro Aluno,</p>
		<p class="texto">Para que voc� realize sua matr�cula, � importante que avalie suas turmas do semestre ${avaliacaoInstitucional.ano}.${avaliacaoInstitucional.periodo}. A avalia��o � utilizada pela Reitoria para definir as a��es de melhorias.</p>
		<p class="texto">Apesar de voc� estar identificado no sistema, o que � necess�rio para recuperar as suas turmas, a avalia��o � SIGILOSA. O sistema n�o relacionar� as respostas ao seu usu�rio. Dessa forma, sinta-se � vontade para avaliar.</p>
		<p class="texto">A ${ configSistema['siglaInstituicao'] } conta com a fidedignidade das suas informa��es, pois elas ser�o o norte da pol�tica universit�ria.</p>
		<%-- <p class="texto"><a href="http://ufrn.br/ufrn2/avaliacao/files/documentos/avaliacaodocencia200810.ppt">Clique aqui para ver o resultado da avalia��o de 2008.1 e conhe�a mais um pouco sobre a nossa Institui��o</a>.</p> --%>
		<p class="assinatura">Atenciosamente,<br />Comiss�o Pr�pria de Avalia��o<br /><a href="mailto:cpa@proplan.ufrn.br">cpa@proplan.ufrn.br</a></p> 
	</div>
	<div class="botao left">
		<h:commandLink action="#{ avaliacaoInstitucional.avaliacaoDiscente }">
			<f:verbatim><span>Preencher Avalia��o</span></f:verbatim>
		</h:commandLink>
	</div>
	<c:if test="${ acesso.discenteAptoAvaliacaoInstitucional }">
	<div class="botao_depois right">
			<a href="${ctx}/verPortalDiscente.do"><span>Avaliar Depois</span></a>
	</div>
	</c:if>
</div>

<div class="bloco_video">
	<!-- player container-->
	<a
		href="http://www.sistemas.ufrn.br/videos/avaliacaoDocencia.flv"
		style="display:block; width:280px; height:214px; margin: 0 0 10px;"
		id="player">

		<!-- splash image inside the container -->
		<img src="${ctx}/avaliacao/img/avaliacaoDiscente.jpg" alt="Avalia��o da doc�ncia" />
	</a>
	
	<script type="text/javascript">
		flowplayer("player", {
			src: "http://www.sistemas.ufrn.br/flowplayer/flowplayer-3.2.2.swf",
			wmode: 'opaque'
		});
	</script>

	<p>
		Assista aqui o v�deo sobre a avalia��o da doc�ncia! 
	</p>
</div>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

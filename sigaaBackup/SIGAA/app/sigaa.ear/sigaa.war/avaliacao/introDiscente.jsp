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

<h2>Avaliação Institucional</h2>

<div class="intro-aval">
	<h4>A avaliação da docência agora é On-line!</h4>
	<h5>Contribua para uma ${ configSistema['siglaInstituicao'] } de maior qualidade!</h5>
	<div class="textos">
		<p class="cumprimento">Caro Aluno,</p>
		<p class="texto">Para que você realize sua matrícula, é importante que avalie suas turmas do semestre ${avaliacaoInstitucional.ano}.${avaliacaoInstitucional.periodo}. A avaliação é utilizada pela Reitoria para definir as ações de melhorias.</p>
		<p class="texto">Apesar de você estar identificado no sistema, o que é necessário para recuperar as suas turmas, a avaliação é SIGILOSA. O sistema não relacionará as respostas ao seu usuário. Dessa forma, sinta-se à vontade para avaliar.</p>
		<p class="texto">A ${ configSistema['siglaInstituicao'] } conta com a fidedignidade das suas informações, pois elas serão o norte da política universitária.</p>
		<%-- <p class="texto"><a href="http://ufrn.br/ufrn2/avaliacao/files/documentos/avaliacaodocencia200810.ppt">Clique aqui para ver o resultado da avaliação de 2008.1 e conheça mais um pouco sobre a nossa Instituição</a>.</p> --%>
		<p class="assinatura">Atenciosamente,<br />Comissão Própria de Avaliação<br /><a href="mailto:cpa@proplan.ufrn.br">cpa@proplan.ufrn.br</a></p> 
	</div>
	<div class="botao left">
		<h:commandLink action="#{ avaliacaoInstitucional.avaliacaoDiscente }">
			<f:verbatim><span>Preencher Avaliação</span></f:verbatim>
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
		<img src="${ctx}/avaliacao/img/avaliacaoDiscente.jpg" alt="Avaliação da docência" />
	</a>
	
	<script type="text/javascript">
		flowplayer("player", {
			src: "http://www.sistemas.ufrn.br/flowplayer/flowplayer-3.2.2.swf",
			wmode: 'opaque'
		});
	</script>

	<p>
		Assista aqui o vídeo sobre a avaliação da docência! 
	</p>
</div>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

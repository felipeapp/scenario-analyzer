<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Avaliação de Projetos de Apoio a Novos Pesquisadores </h2>
		
	<center>
		<h:messages/>
		<div class="infoAltRem">
	        <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Avaliar <br/>
		</div>
	</center>
	
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption> Projetos de Apoio a Novos Pesquisadores (${ fn:length(avaliacaoProjetoApoioNovosPesquisadoresMBean.projetos) }) </caption>
			
			<thead>
				<tr>
					<th>Unidade</th>
					<th>Título do Projeto</th>
					<th>Coordenador</th>
					<th>Status</th>
					<th></th>
				</tr>
			</thead>

			<c:forEach items="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projetos }" var="linha">
				<tr>
					<td> ${ linha.projeto.unidade.sigla } </td>
					<td> ${ linha.projeto.titulo } </td>
					<td> ${ linha.coordenador.pessoa.nome } </td>
					<td> ${ linha.projeto.situacaoProjeto.descricao } </td>
					<td width="20">
						<h:commandLink id="lnkAvaliar" action="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.iniciarCriarAvaliacao }" >
							<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Avaliar"/>
							<f:param name="id" value="#{ linha.id }"/>
						</h:commandLink>
					</td>
				</tr>				
			</c:forEach>
		</table>
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
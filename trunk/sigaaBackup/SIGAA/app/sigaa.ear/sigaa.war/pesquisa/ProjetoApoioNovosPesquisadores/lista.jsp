<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Visualização dos Projetos para ação de Apoio a Novos Pesquisadores </h2>

	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{ projetoApoioNovosPesquisadoresMBean.preCadastrar }" value="Cadastrar"/>
					<h:graphicImage value="/img/buscar.gif" style="overflow: visible;"/>: Visualizar
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			</div>
	</center>

	<h:form id="form">
		<table class="formulario" width="100%">
			<caption> Projetos de Apoio a Novos Pesquisadores </caption>
			
			<thead>
				<tr>
					<th>Título Projeto</th>
					<th>Grupo de Pesquisa</th>
					<th>Situação Projeto</th>
					<th></th>
					<th></th>
					<th></th>
				</tr>
			</thead>

			<c:forEach items="#{ projetoApoioNovosPesquisadoresMBean.projetosApoio }" var="linha">
				<tr>
					<td> ${ linha.projeto.titulo } </td>
					<td> ${ linha.grupoPesquisa.codigo } </td>
					<td> ${ linha.projeto.situacaoProjeto.descricao } </td>
					<td width="20">
						<h:commandLink action="#{ projetoApoioNovosPesquisadoresMBean.view }" >
							<h:graphicImage value="/img/buscar.gif" style="overflow: visible;" title="Visualizar"/>
							<f:param name="id" value="#{ linha.id }"/>
						</h:commandLink>
					</td>
					<c:if test="${ not linha.concluido }">
						<td width="20">
							<h:commandLink action="#{ projetoApoioNovosPesquisadoresMBean.atualizar }" >
								<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar"/>
								<f:param name="id" value="#{ linha.id }"/>
							</h:commandLink>
						</td>
						<td width="20">
							<h:commandLink action="#{ projetoApoioNovosPesquisadoresMBean.remover }" >
								<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
								<f:param name="id" value="#{ linha.id }"/>
							</h:commandLink>
						</td>
					</c:if>
				</tr>				
			</c:forEach>
		</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
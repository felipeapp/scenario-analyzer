<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Projetos de Pesquisa</h2>

<f:view>
	<h:form id="formProjeto" >
	
		<center>
			<div class="infoAltRem">
		        <img src="${ctx}/img/view.gif" alt="Visualizar Projeto" title="Visualizar Projeto"/> Visualizar Projeto
		        <img src="${ctx}/img/requisicoes.png" alt="Listar Planos de Trabalho" title="Listar Planos de Trabalho"/> Listar Planos de Trabalho
		        <img src="${ctx}/img/monitoria/document_view.png" alt="Visualizar Relatório do Projeto" title="Visualizar Relatório do Projeto"/> Visualizar Relatório do Projeto <br/>
			</div>
		</center>
	
		<table class="listagem">
		<caption>Projetos de Pesquisa (${ fn:length(portalConsultorMBean.projetos) })</caption>
			<thead>
				<tr>
					<th> Código </th>
					<th> Título/Coordenador </th>
					<th>  </th>
					<th>  </th>
					<th>  </th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="projetoPesquisa" items="#{ portalConsultorMBean.projetos }" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td> ${ projetoPesquisa.codigo } </td>
						<td> 
							${ projetoPesquisa.titulo } <br/>
							<i>${ projetoPesquisa.coordenador.pessoa.nome }</i>
						</td>
						<td>
							<h:commandLink title="Visualizar Projeto" action="#{portalConsultorMBean.verProjeto}">
								<f:param name="id" value="#{projetoPesquisa.id}"/>
								<h:graphicImage url="/img/view.gif"/>
							</h:commandLink>
						</td>
						<td>
							<h:commandLink title="Listar Planos de Trabalho" action="#{portalConsultorMBean.listarPlanos}">
								<f:param name="id" value="#{projetoPesquisa.id}"/>
								<h:graphicImage url="/img/requisicoes.png"/>
							</h:commandLink>
						</td>
						<td>
							<h:commandLink title="Visualizar Relatório do Projeto" action="#{portalConsultorMBean.verRelatorioProjeto}">
								<f:param name="id" value="#{projetoPesquisa.id}"/>
								<h:graphicImage url="/img/monitoria/document_view.png"/>
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
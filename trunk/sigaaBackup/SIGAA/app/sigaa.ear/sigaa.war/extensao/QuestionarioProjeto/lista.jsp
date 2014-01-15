<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Lista dos questionários associados a um grupo </h2>

	<a4j:keepAlive beanName="questionarioProjetoExtensaoMBean" />
	<h:form id="form">
		
		<center>
			<div class="infoAltRem">
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
			    <h:graphicImage value="/img/note.png" style="overflow: visible;" width="18px;"/>: Visualizar Estatísticas das Respostas
			    <h:graphicImage value="/img/consolidacao/planilha.jpg" style="overflow: visible;" width="18px;"/>: Exportar Respostas <br />
			    <h:graphicImage value="/img/check.png" style="overflow: visible;" width="18px;"/>: Ativar Obrigatóriedade
			    <h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" width="18px;"/>: Desativar Obrigatóriedade
			</div>
		</center>
		
		<table class="formulario" width="100%">
			<caption class="listagem">Associar Questionário</caption>	
			      <thead>
			      	<tr>
			        	<th>Questionário</th>	        	
			        	<th>Grupo de Usuários</th>
			        	<th>Tipo Ação Extensão</th>
			        	<th style="text-align: center;">Período</th>
			        	<th style="text-align: center;">Obrigatório</th>
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>
			        </tr>
			 	</thead>

				<c:forEach items="#{ questionarioProjetoExtensaoMBean.questionarios }" var="item" varStatus="status">
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td> ${ item.questionario.titulo } </td>
						<td> ${ item.nomeGrupo } </td>
						<td> ${ item.tipoAtividade.descricao } </td>
						<td style="text-align: center;"> 
							<fmt:formatDate value="${ item.questionario.inicio }" pattern="dd/MM/yyyy"/> até
							<fmt:formatDate value="${ item.questionario.fim }" pattern="dd/MM/yyyy"/>
						</td> 
						<td style="text-align: center;"> <ufrn:format type="SimNao" valor="${item.obrigatoriedade}" /> </td>
						<td width="20">
							<h:commandLink action="#{questionarioProjetoExtensaoMBean.remover}" onclick="#{confirmDelete}" >
								<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
								<f:param name="idQuestionario" value="#{item.questionario.id}"/>
								<f:param name="tipoGrupo" value="#{item.tipoGrupo}"/>
								<f:param name="idTipoAtividade" value="#{item.tipoAtividade.id}" />
							</h:commandLink>
						</td>
						<td>
							<h:commandLink title="Visualizar Estatísticas das Respostas" action="#{ questionarioProjetoExtensaoMBean.verEstatistica}">
								<f:param name="id" value="#{item.questionario.id}" />
								<f:param name="idTipoAtividade" value="#{item.tipoAtividade.id}" />
								<h:graphicImage url="/img/note.png" width="18px;"/>
							</h:commandLink>
						</td>
						<td>
							<h:commandLink title="Exportar Respostas" action="#{ questionarioProjetoExtensaoMBean.exportarRespostas}">
								<f:param name="idQuestionario" value="#{item.questionario.id}"/>
								<f:param name="tipoGrupo" value="#{item.tipoGrupo}"/>
								<f:param name="idTipoAtividade" value="#{item.tipoAtividade.id}" />
								<h:graphicImage url="/img/consolidacao/planilha.jpg" width="18px;"/>
							</h:commandLink>
						</td>
						<td width="20">
							<h:commandLink action="#{ questionarioProjetoExtensaoMBean.mudarObrigatoriedade }">
								<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" rendered="#{ not item.obrigatoriedade }" title="Ativar Obrigatóriedade"/>
								<h:graphicImage value="/img/check.png" style="overflow: visible;" rendered="#{ item.obrigatoriedade }" title="Desativar Obrigatóriedade"/>
								<f:param name="idQuestionario" value="#{item.questionario.id}"/>
								<f:param name="tipoGrupo" value="#{item.tipoGrupo}"/>
								<f:param name="idTipoAtividade" value="#{item.tipoAtividade.id}" />
								<f:param name="obrigatoriedade" value="#{item.obrigatoriedade}" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
		</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
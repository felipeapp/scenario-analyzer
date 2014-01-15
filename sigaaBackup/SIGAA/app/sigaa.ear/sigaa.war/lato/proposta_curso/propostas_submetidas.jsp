<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
  <h2><ufrn:subSistema /> &gt; Minhas Propostas</h2>
	<h:form id="form">
		<center>
			<div class="infoAltRem">
					<h:commandLink 	action="#{cursoLatoMBean.preCadastrar}">
						<h:graphicImage url="/img/adicionar.gif" /> Cadastrar Nova Proposta
					</h:commandLink> <br />
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Continuar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
			        <h:graphicImage value="/img/buscar.gif" style="overflow: visible;"/>: Visualizar 
			        <h:graphicImage value="/img/requisicoes.png" style="overflow: visible;"/>: Visualizar Despacho
			        <h:graphicImage value="/img/copia.png" style="overflow: visible; width: 18px;"/>: Aproveitar Dados da Proposta
			</div>
		</center>

		<table class=formulario width="100%">
 			<caption class="listagem">Minhas Propostas</caption>
				<thead>
					<tr>
						<th>Nome Curso</th>
						<th>Situação</th>
						<th style="text-align: right;">Carga Horária</th>						
						<th style="text-align: center;">Data de Início</th>
						<th style="text-align: center;">Data Final</th>
						<th colspan="5"></th>
					</tr>
				</thead>

				<c:forEach items="#{cursoLatoMBean.allCursoLatoDocente}" var="cursoLato" varStatus="status">
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td width="250">${cursoLato.nomeCurso} ${cursoLato.nome}</td>
								<td>${cursoLato.propostaCurso.situacaoProposta.descricao }</td>
								<td style="text-align: right">${cursoLato.cargaHoraria }</td>
								<td style="text-align: center;"><ufrn:format valor="${cursoLato.dataInicio}" type="data" /></td>
								<td style="text-align: center;"><ufrn:format valor="${cursoLato.dataFim}" type="data" /></td>
								
								<c:if test="${ not cursoLato.propostaCurso.situacaoProposta.valida }">
									<td width="20">
										<h:commandLink action="#{cursoLatoMBean.carregaObject}" >
											<h:graphicImage value="/img/seta.gif"style="overflow: visible;" title="Continuar"/>
											<f:param name="id" value="#{cursoLato.id}"/>
										</h:commandLink>
									</td>
									<td width="20">
										<h:commandLink action="#{cursoLatoMBean.remocao}" onclick="#{confirmDelete}" >
											<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
											<f:param name="id" value="#{cursoLato.id}"/>
										</h:commandLink>
									</td>
									<td width="20">
										<h:commandLink action="#{cursoLatoMBean.viewDespacho}" >
											<h:graphicImage value="/img/requisicoes.png" style="overflow: visible;" title="Visualizar Despacho" />
											<f:param name="id" value="#{cursoLato.id}"/>
										</h:commandLink>
									</td>
									
									<td width="20"></td>
								</c:if>

							    <c:if test="${ cursoLato.propostaCurso.situacaoProposta.valida }">
									<td colspan="3"></td>
									<td  style="text-align: right;">
										<h:commandLink action="#{cursoLatoMBean.cadastrarCursoBaseadoEmCursoAntigo}" >
											<h:graphicImage value="/img/copia.png" style="overflow: visible; width: 18px;" title="Aproveitar Dados da Proposta"/>
											<f:param name="id" value="#{ cursoLato.id }"/>
										</h:commandLink>
									</td>					
								</c:if>
								
								<td width="20" colspan="${cursoLato.propostaCurso.situacaoProposta.valida ? '4' : '1'}" align="right">
									<h:commandLink action="#{cursoLatoMBean.visualizar}" >
										<h:graphicImage value="/img/buscar.gif" style="overflow: visible;" title="Visualizar" />
										<f:param name="id" value="#{cursoLato.id}"/>
									</h:commandLink>
								</td>
								
							</tr>
	   			</c:forEach>
		  </table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
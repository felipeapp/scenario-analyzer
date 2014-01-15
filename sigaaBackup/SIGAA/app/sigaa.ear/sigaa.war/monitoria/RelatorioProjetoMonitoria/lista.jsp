<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h:outputText  value="#{projetoMonitoria.create}"/>
	<h:outputText  value="#{relatorioProjetoMonitoria.create}"/>
	

	<h2><ufrn:subSistema /> > Relatórios de Projetos de Monitoria</h2>

	<div class="infoAltRem">
		<c:if test="${acesso.coordenadorMonitoria}">
		    <h:graphicImage value="/img/monitoria/document_edit.png" style="overflow: visible;" id="cadp"/>: Cadastrar Relatório Parcial
		    <h:graphicImage value="/img/monitoria/document_ok.png" style="overflow: visible;" id="cadf"/>: Cadastrar Relatório Final<br/>
		    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;" id="altr"/>: Alterar Relatório	    
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;" id="relv"/>: Visualizar Relatório
		    <h:graphicImage value="/img/monitoria/form_blue.png" style="overflow: visible;"/>: Visualizar Avaliações do Relatório
	    </c:if>
	</div>


<h:form>
	<table class="listagem">
		<caption class="listagem">Projetos de Ensino Coordenados ou Criados pelo usuário atual </caption>

		<c:forEach items="#{projetoMonitoria.projetosAtivosCoordenadosUsuarioLogado}" var="projeto" varStatus="status">
			<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
				<td>${projeto.anoTitulo}</td>
				<td>
					<h:commandLink title="Cadastrar Relatório Parcial" action="#{relatorioProjetoMonitoria.iniciarRelatorioParcial}">
					     <f:param name="id" value="#{projeto.id}"/>
					     <h:graphicImage url="/img/monitoria/document_edit.png" />
					</h:commandLink>
				</td>
				<td>
					<h:commandLink title="Cadastrar Relatório Final" action="#{relatorioProjetoMonitoria.iniciarRelatorioFinal}">
					     <f:param name="id" value="#{projeto.id}"/>
					     <h:graphicImage url="/img/monitoria/document_ok.png" />
					</h:commandLink>
				</td>
			</tr>
		
			<tr>
				<td colspan="3">
					<table class="listagem">
						<thead>
							<tr>
								<th>Tipo de Relatório</th>
								<th style="text-align:center">Data do Cadastro</th>
								<th style="text-align:center">Data do Envio</th>				
								<th>Situação</th>
								<th></th>				
								<th></th>
								<th></th>
							</tr>
						</thead>
				
						<c:forEach items="#{projeto.relatoriosProjetoMonitoria}" var="rela" varStatus="status">
				            <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				            
				                <td> ${rela.tipoRelatorio.descricao} </td>
					            <td style="text-align:center"><fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${rela.dataCadastro}"/></td>
					            <td style="text-align:center"><fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${rela.dataEnvio}"/></td>
					            <td> ${rela.status.descricao} </td>

								<td width="2%">
									<h:commandLink title="Alterar Relatório" action="#{relatorioProjetoMonitoria.atualizar}" 
									rendered="#{rela.permitidoAlterar}" id="btalterar">
									     <f:param name="id" value="#{rela.id}"/>
									     <h:graphicImage url="/img/alterar.gif" />
									</h:commandLink>
								</td>
								
								<td width="2%">
									<h:commandLink title="Visualizar Relatório" action="#{relatorioProjetoMonitoria.view}" id="btview">
									     <f:param name="id" value="#{rela.id}"/>
									     <h:graphicImage url="/img/view.gif" />
									</h:commandLink>
								</td>
								
								<td width="2%">															
									<c:if test="${rela.status.id  == 3 }">
										<h:commandLink title="Visualizar Avaliações do Relatório" action="#{avalRelatorioProjetoMonitoria.avaliacoesRelatorioForward}" style="border: 0;">
										   	<f:param name="idRelatorio" value="#{rela.id}"/>
									     	<h:graphicImage url="/img/monitoria/form_blue.png" id="view"/>
										</h:commandLink>	
									</c:if>														
								</td>								
							</tr>
						</c:forEach>
				
						<c:if test="${empty projeto.relatoriosProjetoMonitoria}">
							<tr>
								<td colspan="4"><center><font color="red">Não há relatórios cadastrados para este projeto<br/></font> </center></td>
							</tr>
						</c:if>
				
					</table>
				
				</td>
			</tr>
			
			<tr><td colspan="3"><br/></td></tr>
			
			
		</c:forEach>

		<c:if test="${empty projetoMonitoria.projetosAtivosCoordenadosUsuarioLogado}">
			<tr>
				<td colspan="3"><center><font color="red">O usuário não é Coordenador de Projetos de Ensino Ativos<br/></font> </center></td>
			</tr>
		</c:if>


	</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
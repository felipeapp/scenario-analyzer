<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>
	<h2><ufrn:subSistema /> > Seleção de Monitores</h2>

	<div class="infoAltRem">
			<h:graphicImage value="/img/view.gif"style="overflow: visible;" />: Visualizar Projeto de Ensino
		    <h:graphicImage value="/img/monitoria/form_green.png"style="overflow: visible;"/>: Visualizar Candidatos <br/>		    
		    <h:graphicImage value="/img/monitoria/document_chart.png"style="overflow: visible;"/>: Visualizar Resultado
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;" />: Alterar Dados da Prova							
	     	<h:graphicImage value="/img/seta.gif"style="overflow: visible;" />: Validar Prova Seletiva
	</div>

<h:form>

	<c:set value="#{validaSelecaoMonitor.obj}" var="projeto"/>
	<table class="listagem">
		<caption class="listagem">Lista de provas do projeto</caption>

			<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
				<td>${projeto.anoTitulo}</td>
				<td>
					<h:commandLink title="Visualizar Projeto" action="#{projetoMonitoria.view}" id="cmdViewProjeto">
					     <f:param name="id" value="#{projeto.id}"/>
					     <h:graphicImage url="/img/view.gif" />
					</h:commandLink>
				</td>
			</tr>
		
			<tr>
				<td colspan="2">
					<table class="listagem">
						<thead>
							<tr>
								<th>Título</th>
								<th>Data da Prova</th>
								<th>Situação da Prova</th>
								<th width="3%">VR</th>
								<th width="3%">VNR</th>
								<th></th>
								<th></th>				
								<th></th>				
								<th></th>
							</tr>
						</thead>
				
						<c:forEach items="#{projeto.provasSelecao}" var="prova" varStatus="status">
						
							<%-- prova.ativo = false é prova removida --%>
						   <c:if test="${prova.ativo}">
						   						   
					            <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					            	<td>${prova.titulo}</td>					            
						            <td><fmt:formatDate pattern="dd/MM/yyyy" value="${prova.dataProva}"/></td>
						            <td>${prova.situacaoProva.descricao}</td>
					                <td>${prova.vagasRemuneradas}</td>
					                <td>${prova.vagasNaoRemuneradas}</td>
	
									<td  width="2%">			
										<h:commandLink  title="Visualizar Candidatos" action="#{provaSelecao.visualizarCandidatos}" style="border: 0;" id="cmdVerCandidatos">
											      <f:param name="id" value="#{prova.id}"/>
											      <h:graphicImage url="/img/monitoria/form_green.png" />
										</h:commandLink>
									</td>
									
									<td width="2%">			
										<h:commandLink title="Visualizar Resultado"  action="#{ provaSelecao.visualizarResultados }" id="cmdVerResultadoSelecao">
											      <f:param name="id" value="#{prova.id}"/>
											      <h:graphicImage url="/img/monitoria/document_chart.png" />
										</h:commandLink>
									</td>
									
									<td width="2%">			
										<h:commandLink  title="Alterar Dados da Prova" action="#{provaSelecao.atualizar}" style="border: 0;"  rendered="#{acesso.monitoria}" id="cmdAlterarProva">
											      <f:param name="id" value="#{prova.id}"/>													      
											      <h:graphicImage url="/img/alterar.gif" />
										</h:commandLink>
									</td>									
																		
									<td width="2%">
										<h:commandLink title="Validar Prova Seletiva" action="#{validaSelecaoMonitor.selecionarProva}" style="border: 0;" rendered="#{acesso.monitoria}" id="cmdValidarProva">
										      <f:param name="id" value="#{prova.id}"/>
										      <h:graphicImage url="/img/seta.gif" />
										</h:commandLink>
									</td>																											
								</tr>
								
							</c:if>
						</c:forEach>
				
						<c:if test="${empty projeto.provasSelecao}">
							<tr>
								<td colspan="10"><center><font color="red">Não há Provas de Seleção cadastradas para este projeto<br/></font> </center></td>
							</tr>
						</c:if>
				
					</table>
				
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<center><h:commandButton value="<< Voltar" action="#{ validaSelecaoMonitor.filtrar }" title="Voltar" id="btVoltar" /></center>
					 </td>
				</tr>
			</tfoot>
	</table>
	
	</h:form>
	<div>[<b>VR</b> = Vagas Remuneradas  <b>VNR</b> = Vagas Não Remuneradas] </div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
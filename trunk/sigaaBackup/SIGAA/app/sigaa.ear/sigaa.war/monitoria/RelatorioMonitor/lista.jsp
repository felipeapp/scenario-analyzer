<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>	

<f:view>

	<%@include file="/portais/discente/menu_discente.jsp"%>
	<h2><ufrn:subSistema /> > Relatórios de Monitoria</h2>

		<div class="descricaoOperacao">
			<font color="red">Atenção:</font> 
			Somente discentes ativos podem enviar Relatórios Parciais e Finais.<br>
			O Relatório de Desligamento deve ser enviado somente quando o discente desejar se desvincular do projeto antes do seu término.<br>
		</div>	

		<div class="infoAltRem">
		    <h:graphicImage value="/img/monitoria/document_edit.png" style="overflow: visible;"/>: Cadastrar Relatório Parcial
		    <h:graphicImage value="/img/monitoria/document_ok.png" style="overflow: visible;"/>: Cadastrar Relatório Final	    
		    <h:graphicImage value="/img/monitoria/document_new.png" style="overflow: visible;"/>: Cadastrar Relatório de Desligamento<br/>		    
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;" id="relremove"/>: Remover Relatório
		    <h:graphicImage value="/img/seta.gif" style="overflow: visible;" id="altr"/>: Alterar/Enviar Relatório	    
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;" id="relv"/>: Visualizar Relatório
		</div>
		
		
	<h:form>
	 <table class="listagem">
		<caption class="listagem">Lista de Projetos do Monitor</caption>

		<c:forEach items="#{relatorioMonitor.discentesMonitoria}" var="dm" varStatus="status">
			<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
				<td>${dm.projetoEnsino.anoTitulo}</td>
				<c:choose>
				<c:when test="${dm.ativo}">
				<td>
					<h:commandLink title="Cadastrar Relatório Parcial..." action="#{relatorioMonitor.iniciarRelatorioParcial}"  
						style="border: 0;" rendered="#{dm.cadastrarRelatorios}">
					     <f:param name="id" value="#{dm.id}"/>
					     <h:graphicImage url="/img/monitoria/document_edit.png" />
					</h:commandLink>
				</td>
				<td>
					<h:commandLink title="Cadastrar Relatório Final..." action="#{relatorioMonitor.iniciarRelatorioFinal}"  
						style="border: 0;" rendered="#{dm.cadastrarRelatorios}">
					     <f:param name="id" value="#{dm.id}"/>
					     <h:graphicImage url="/img/monitoria/document_ok.png" />
					</h:commandLink>
				</td>				
				<td>
					<h:commandLink title="Cadastrar Relatório de Desligamento..." action="#{relatorioMonitor.iniciarRelatorioDesligamento}" 
						style="border: 0;" rendered="#{dm.cadastrarRelatorios}">	
					   	<f:param name="id" value="#{dm.id}"/>				    	
						<h:graphicImage url="/img/monitoria/document_new.png"/>
					</h:commandLink>
				</td>						
				</c:when>
				<c:otherwise>
				<td colspan="3"></td>
				</c:otherwise>
				</c:choose>
			</tr>		
			<tr>
				<td colspan="4">
					<table class="listagem">
						<thead>
							<tr>
								<th>Tipo de Relatório</th>
								<th style="text-align: center">Data do Cadastro</th>
								<th style="text-align: center">Data do Envio</th>				
								<th>Situação</th>
								<th></th>				
								<th></th>								
								<th></th>
							</tr>
						</thead>
				
						<c:forEach items="#{dm.relatoriosMonitor}" var="rela" varStatus="status">
				            <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				            
				                <td> ${rela.tipoRelatorio.descricao} </td>
					            <td style="text-align: center"><fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${rela.dataCadastro}"/></td>
					            <td style="text-align: center"><fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${rela.dataEnvio}"/></td>
					            <td> ${rela.status.descricao} </td>

								<td width="2%">
									<h:commandLink title="Alterar/Enviar Relatório" action="#{relatorioMonitor.atualizar}" 
									rendered="#{ !rela.enviado }" id="btalterar">
									     <f:param name="id" value="#{rela.id}"/>
									     <h:graphicImage url="/img/seta.gif" />
									</h:commandLink>
								</td>

								<td width="2%">
									<h:commandLink title="Remover Relatório" action="#{relatorioMonitor.preRemoverRelatorio}" id="btremover" rendered="#{empty rela.dataEnvio}">
									     <f:param name="id" value="#{rela.id}"/>
									     <h:graphicImage url="/img/delete.gif" />
									</h:commandLink>
								</td>
								
								<td width="2%">
									<h:commandLink title="Visualizar Relatório" action="#{relatorioMonitor.view}" id="btview">
									     <f:param name="id" value="#{rela.id}"/>
									     <h:graphicImage url="/img/view.gif" />
									</h:commandLink>
								</td>
								
							</tr>
						</c:forEach>
				
						<c:if test="${empty dm.relatoriosMonitor}">
							<tr>
								<td colspan="4"><center><font color="red">Não há relatórios cadastrados para este projeto<br/></font> </center></td>
							</tr>
						</c:if>
				
					</table>
				
				</td>
			</tr>
			
			<tr><td colspan="3"><br/></td></tr>
			
			
		</c:forEach>
		
		<c:if test="${empty relatorioMonitor.discentesMonitoria}">            
			<tr>
				<td colspan="4"><center><font color="red">O usuário atual não é Monitor Projetos Ativos.<br/></font> </center></td>
			</tr>
		</c:if>

	</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
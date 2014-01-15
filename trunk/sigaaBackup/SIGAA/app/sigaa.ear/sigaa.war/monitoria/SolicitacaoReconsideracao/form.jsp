<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h2><ufrn:subSistema /> > Solicita��o de Reconsidera��o de Avalia��o de Projeto</h2>
	
	<h:form id="form">
	
		<h:inputHidden value="#{solicitacaoReconsideracao.confirmButton}" id="confirmButton"/>
		<h:inputHidden value="#{solicitacaoReconsideracao.obj.id}" id="idSolicitacao"/>
		<h:inputHidden value="#{solicitacaoReconsideracao.obj.projeto.id}" id="id"/>
		
		<table class="formulario" width="100%">
			<caption class="listagem"> Solicita��o de Reconsidera��o de Avalia��o </caption>
			
				<c:if test="${not empty solicitacaoReconsideracao.projetosMonitoriaReconsideraveis}">
			
					<tr>
						<th><b>T�tulo: </b></th>
						<td> 	<h:outputText value="#{solicitacaoReconsideracao.obj.projeto.titulo}"/></td>
					</tr>
					
					<tr>
						<th><b>Situa��o: </b></th>
						<td><h:outputText value="#{solicitacaoReconsideracao.obj.projeto.situacaoProjeto.descricao}"/></td>
					</tr>
	
					<tr>
						<th><b>Coordena��o: </b></th>
						<td><h:outputText value="#{solicitacaoReconsideracao.obj.projeto.coordenador.pessoa.nome}"/></td>
					</tr>
	

					<tr>
						<td colspan="2" class="subFormulario">Lista de avalia��es para este projeto</td>
					</tr>	
	
					<tr>				
						<td colspan="2">
							<t:dataTable value="#{ solicitacaoReconsideracao.obj.projetoMonitoria.avaliacoes }" var="avaliacao" width="100%" rowIndexVar="index" forceIdIndex="true">
								<t:column>
									<h:outputText value="<b>Avalia��o #{index+1}: </b>" escape="false"/>
									<f:verbatim><br/><b>Data da Avalia��o: </b></f:verbatim><h:outputText value="#{avaliacao.dataAvaliacao}"><f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss"/></h:outputText>
									<f:verbatim><br/><b>Nota: </b></f:verbatim>
									<h:outputText value="#{avaliacao.notaAvaliacao}" rendered="#{not empty avaliacao.notaAvaliacao}"/>									
									<h:outputText value="--" rendered="#{empty avaliacao.notaAvaliacao}"/>
									<f:verbatim><br/><b>Parecer do Avaliador:</b><br/></f:verbatim>
									<h:outputText value="#{avaliacao.parecer}"/>
									<f:verbatim><br/></f:verbatim>
								</t:column>
							</t:dataTable>
			 	   		</td>
					 </tr>

					<tr>
						<td colspan="2" class="subFormulario">Requerimento</td>
					</tr>	
			
					 <tr>
						<td colspan="2">
							Justificativa da Solicita��o: <h:graphicImage url="/img/required.gif" style="vertical-align: top;"/><br/>
							<h:inputTextarea value="#{solicitacaoReconsideracao.obj.justificativa}" rows="10" style="width:99%" readonly="#{solicitacaoReconsideracao.readOnly}" id="justificativa"/>
							<br/>
						</td>
					 </tr>
				<tfoot>
					<tr>
						<td colspan="2">
							<c:if test="${solicitacaoReconsideracao.confirmButton == 'Remover'}">
								<h:commandButton value="#{solicitacaoReconsideracao.confirmButton}" action="#{solicitacaoReconsideracao.remover}"/>
							</c:if>
		
							<c:if test="${solicitacaoReconsideracao.confirmButton != 'Remover'}">
								<h:commandButton value="Cadastrar" action="#{solicitacaoReconsideracao.cadastrarSolicitacaoMonitoria}"/>
							</c:if>
		
							<h:commandButton value="Cancelar" action="#{solicitacaoReconsideracao.cancelar}" onclick="#{confirm}"/>
						</td>
					</tr>
				</tfoot>
			</c:if>
			<c:if test="${empty solicitacaoReconsideracao.projetosMonitoriaReconsideraveis}">
				<tr>
					<td>
						 <br/>
				         <center><font color="red">N�o h� Projetos de Monitoria pass�veis de reconsidera��o <br/> ou o usu�rio atual n�o � coordenador de projetos ativos.</font></center>
				         <br/>
			        </td>
				</tr>
			</c:if>
	</table>
	
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/> 
		<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
	</center>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
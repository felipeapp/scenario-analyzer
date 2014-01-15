<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.esquerda{text-align: left !important;}
	.direita{text-align: right !important;}
	.centro{text-align: center !important;}
	.instrutores{width: 26%;}
	.nome{width: 27%;}
	.periodo{width: 13%;}
</style>

<style>
	.colSiape,.colDocente,.colEmail,.colSituacao{text-align: left !important;}
	.colSiape,.colSituacao{width: 10%;}
	.colNome{text-align: left !important;}
	.colSol{text-align: right !important;width: 20%;}
</style>

<f:view>
	
	<a4j:keepAlive beanName="relatorioAtividadeAP"></a4j:keepAlive>
	<h2 class="title">
		<ufrn:subSistema /> > Detalhes da Atividade de Atualização Pedagógica
	</h2>
			
	<c:if test="${relatorioAtividadeAP.obj.idArquivo > 0}">
	<center>
		<div class="infoAltRem">
		    <img src="/shared/img/icones/download.png" style="overflow: visible;"/>: Visualizar Programa
		</div>
	</center>
	</c:if>
	
	<h:form id="formDetalhesAitividade">	
	<table class="visualizacao" width="70%">
		<caption class="visualizacao">Dados da Atividade</caption>

		<tr>
			<th width="30%">Nome:</th>
			<td>
				<h:outputText value="#{relatorioAtividadeAP.obj.nome}"/>	
			</td>
		</tr> 
		
		<tr>
			<th>Período:</th>
			<td>
				<h:outputText value="#{relatorioAtividadeAP.obj.descricaoPeriodo}" ></h:outputText>	
			</td>
		</tr>
		
		<tr>
			<th>Horário:</th>
			<td>
				<h:outputText value="#{relatorioAtividadeAP.obj.horarioInicio} à #{relatorioAtividadeAP.obj.horarioFim}" rendered="#{not empty relatorioAtividadeAP.obj.horarioInicio}"/>
				<h:outputText value="Não Informado" rendered="#{empty relatorioAtividadeAP.obj.horarioInicio}"/>	
			</td>
		</tr>
		
		<tr>
			<th>Carga Horária:</th>
			<td>
				<h:outputText value="#{relatorioAtividadeAP.obj.ch}h" rendered="#{not empty relatorioAtividadeAP.obj.ch}"/>
				<h:outputText value="Não Informado" rendered="#{empty relatorioAtividadeAP.obj.ch}"/>	
			</td>
		</tr>
		
		<tr>
			<th>Nº. de Vagas:</th>
			<td>
				<h:outputText value="#{relatorioAtividadeAP.obj.numVagas}" rendered="#{not empty relatorioAtividadeAP.obj.numVagas}"/>
				<h:outputText value="Não Informado" rendered="#{empty relatorioAtividadeAP.obj.numVagas}"/>	
			</td>
		</tr>
		
		<tr>
			<th>Professores:</th>
			<td>
				<h:outputText value="#{relatorioAtividadeAP.obj.descricaoInstrutores}" ></h:outputText>	
			</td>
		</tr>
		
		<tr>
			<th>Arquivo em Anexo:</th>
			<td>
				<c:choose>
					<c:when test="${relatorioAtividadeAP.obj.idArquivo > 0}">
						<a href="/sigaa/verProducao?idProducao=${relatorioAtividadeAP.obj.idArquivo}&key=${ sf:generateArquivoKey(relatorioAtividadeAP.obj.idArquivo) }"
							title="Visualizar Programa" target="_blank">
							<img src="/shared/img/icones/download.png"/>
						</a>
					</c:when>
					<c:otherwise>
						Inexistente.					
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		
		<tr>
			<td class="subListagem" colspan="2">Outras informações:</td>
		</tr>
		<tr>	
			<td colspan="2" valign="top" align="justify">
				<h:outputText escape="false" value="#{relatorioAtividadeAP.obj.descricao}" rendered="#{not empty relatorioAtividadeAP.obj.descricao}" ></h:outputText>	
				<h:outputText value="Não informado" rendered="#{empty relatorioAtividadeAP.obj.descricao}" ></h:outputText>
			</td>
		</tr>
			<tr>
				<td class="subListagem" colspan="2">Solicitação de Recursos NEE:</td>
			</tr>
			<tr>	
				<td colspan="2" >
					<c:if test="${not empty relatorioAtividadeAP.recursosNEEAtividade}">
						<t:dataTable id="recursosAtividade" value="#{relatorioAtividadeAP.recursosNEEAtividade}"  rendered="#{not empty relatorioAtividadeAP.participantesAtividade}"
							 var="rec" styleClass="subListagem"  width="60%" headerClass="colNome,colSol" 
							 columnClasses="colNome,colSol" rowClasses="linhaImpar,linhaPar">
									
									<t:column styleClass="colNome" headerstyleClass="colNome">
										<f:facet name="header">
											<f:verbatim>Recurso</f:verbatim>
										</f:facet>
										<h:outputText value="#{rec.tipoRecursoNee.descricao}" rendered="#{!rec.tipoOutros}"/>
										<h:outputText value="#{rec.outros}" rendered="#{rec.tipoOutros}"/>
									</t:column>
									<t:column styleClass="colSol" headerstyleClass="colSol">
										<f:facet name="header">
											<f:verbatim>Num. de Solicitações</f:verbatim>
										</f:facet>
										<h:outputText value="#{rec.solicitacoes}" />
									</t:column>
					
						</t:dataTable>
					</c:if>
					<c:if test="${empty relatorioAtividadeAP.recursosNEEAtividade}">
						<div align="center" style="color:red">Nenhum recurso NEE selecionado</div>
					</c:if>
				</td>
			</tr>
		<tr>
			<td class="subListagem" colspan="2">Participantes:</td>
		</tr>	
		
		<tr>
			<td colspan="2">	
				<t:dataTable id="participantesAtividade" value="#{relatorioAtividadeAP.participantesAtividade}"  rendered="#{not empty relatorioAtividadeAP.participantesAtividade}"
					 var="_reg" styleClass="subListagem"  width="100%" headerClass="colSiape,colDocente,colEmail,colSituacao,icon,icon,icon" 
					 columnClasses="colSiape,colDocente,colEmail,colSituacao,icon,icon,icon" rowClasses="linhaImpar,linhaPar">
							
							<t:column styleClass="colSiape" headerstyleClass="colSiape">
								<f:facet name="header">
									<f:verbatim>Siape</f:verbatim>
								</f:facet>
								<h:outputText value="#{_reg.docente.siape}"/>
							</t:column>
							<t:column styleClass="colDocente" headerstyleClass="colDocente">
								<f:facet name="header">
									<f:verbatim>Participante</f:verbatim>
								</f:facet>
								<h:outputText value="#{_reg.docente.nome}"/>
							</t:column>
		
							<t:column styleClass="colEmail" headerstyleClass="colEmail">
								<f:facet name="header">
									<f:verbatim>E-mail</f:verbatim>
								</f:facet>
								<h:outputText value="#{_reg.docente.pessoa.email}"/> 
							</t:column>
							
							<t:column styleClass="colEmail" headerstyleClass="colEmail">
								<f:facet name="header">
									<f:verbatim>Categoria</f:verbatim>
								</f:facet>
								<h:outputText value="#{_reg.docente.categoria.descricao}"/> 
							</t:column>
							
							<t:column styleClass="colSituacao" headerstyleClass="colSituacao">
								<f:facet name="header">
									<f:verbatim>Situação</f:verbatim>
								</f:facet>
								<h:outputText value="#{_reg.descricaoSituacao}"/> 
							</t:column>
					
				</t:dataTable>
			</td>
		</tr>	
					
	</table>
		
	<br/>
	<center>
		<h:commandButton value="<< Voltar" title="Voltar" id="btnVoltarInscricao" immediate="true"
			 action="#{relatorioAtividadeAP.voltar}">
		</h:commandButton>
		
	</center>
	</h:form>
		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

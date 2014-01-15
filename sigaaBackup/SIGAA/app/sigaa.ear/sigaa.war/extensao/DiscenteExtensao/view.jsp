<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2>Visualização de Discente Extensão</h2>
	<br>

	<table class="tabelaRelatorio" width="100%">
	<caption> Dados de discente de extensão </caption>

<tbody>
	
	<tr>
		<th width="20%"><b>Código:</b></th>
		<td><h:outputText value="#{discenteExtensao.obj.atividade.codigo}"/></td>
	</tr>	

	<tr>
		<th><b>Título da Ação:</b></th>
		<td><h:outputText value="#{discenteExtensao.obj.atividade.titulo}"/></td>
	</tr>	

	<tr>
		<th><b>Discente:</b></th>
		<td><h:outputText value="#{discenteExtensao.obj.discente.matriculaNome}"/></td>
	</tr>

	<tr>
		<th><b>Orientador:</b></th>
		<td><h:outputText value="#{discenteExtensao.obj.planoTrabalhoExtensao.orientador.pessoa.nome}"/></td>
	</tr>

	<tr>
		<th><b>Curso:</b></th>
		<td><h:outputText value="#{discenteExtensao.obj.discente.curso.nomeCompleto}" rendered="#{not empty discenteExtensao.obj.discente.curso}"/></td>
	</tr>
	
	<tr>
		<th><b>Vínculo:</b></th>
		<td><h:outputText value="#{discenteExtensao.obj.tipoVinculo.descricao}"/></td>
	</tr>	

	<tr>
		<th><b>Situação:</b></th>
		<td><h:outputText value="#{discenteExtensao.obj.situacaoDiscenteExtensao.descricao}"/></td>
	</tr>	
	
	<tr>
		<th><b>Data de Início:</b></th>
		<td><h:outputText value="#{discenteExtensao.obj.dataInicio}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText></td>
	</tr>						


	<tr>
		<th><b>Data de Fim:</b></th>
		<td><h:outputText value="#{discenteExtensao.obj.dataFim}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText></td>
	</tr>
	

	<tr>
		<th><b>Cadastrado em:</b></th>
		<td><h:outputText value="#{discenteExtensao.obj.dataCadastro}"><f:convertDateTime pattern="dd/MM/yyyy HH:mm"/></h:outputText></td>
	</tr>						
	


	<%-- somente membros da proex tem acesso aos dados bancários do discente --%>
	<c:if test="${acesso.extensao}">
		<tr>
			<td colspan="2">
				<table class="subFormulario" width="100%">		
					<caption class="listagem"> Dados Bancários </caption>
						<tr>
							<th width="20%"><b>Banco:</b></th>
							<td><h:outputText value="#{discenteExtensao.obj.banco.codigoNome}"/></td>
						</tr>
						<tr>
							<th><b>Agência:</b></th>
							<td><h:outputText value="#{discenteExtensao.obj.agencia}"/></td>
						</tr>
						<tr>
							<th><b>Conta Corrente:</b></th>
							<td><h:outputText value="#{discenteExtensao.obj.conta}"/></td>
						</tr>		
						<c:if test="${discenteExtensao.obj.operacao != null}">
						<tr>
							<th><b>Operação:</b></th>
							<td><h:outputText value="#{discenteExtensao.obj.operacao}"/></td>
						</tr>					
						</c:if>
				</table>
			</td>
		</tr>
	</c:if>


	
	<tr>
		<td colspan="2">
			<table class="subFormulario" width="100%">		
				<caption class="listagem"> Histórico da Situação do Discente </caption>
					<tr>
						<td>
							<t:dataTable value="#{discenteExtensao.obj.historicoSituacao}" var="hs" align="center" width="100%" rendered="#{not empty discenteExtensao.obj.historicoSituacao}">
									<t:column>
										<f:facet name="header">
											<f:verbatim>Discente</f:verbatim>
										</f:facet>
										<h:outputText value="#{hs.discenteExtensao.discente.matriculaNome}" />						
									</t:column>
				
									<t:column>
										<f:facet name="header">
											<f:verbatim>Motivo da substituição</f:verbatim>
										</f:facet>
										<h:outputText value="#{hs.discenteExtensao.motivoSubstituicao}">						
												<f:convertDateTime pattern="dd/MM/yyyy"/>
										</h:outputText>
									</t:column>
									
							</t:dataTable>
						
				
							<c:if test="${empty discenteExtensao.obj.historicoSituacao}">
								<center><font color="red">Sem histórico de situações!</font></center>
							</c:if>
				
						 </td>
					</tr>
				</table>
		</td>
	</tr>
	
	</tbody>
</table>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
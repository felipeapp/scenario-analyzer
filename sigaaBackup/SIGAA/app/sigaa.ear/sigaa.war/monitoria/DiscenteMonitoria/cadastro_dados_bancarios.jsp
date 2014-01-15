<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/discente/menu_discente.jsp" %>

	<h2><ufrn:subSistema /> > Cadastro de Dados Bancários</h2>
	<h:form id="form">
		<h:outputText value="#{discenteMonitoria.create}"/>
		<h:inputHidden value="#{discenteMonitoria.obj.id}"/>

		<table class="formulario" width="100%" cellpadding="3">
		<tbody>
			<caption class="listagem"> Cadastro de Dados Bancários </caption>
	
	
			<tr>
				<th width="20%"> Discente: </th>
				<td> <b> <h:outputText value="#{discenteMonitoria.obj.discente.matriculaNome}" /></b>	</td>
			</tr>
	
			<tr>
				<th> Projeto: </th>
				<td> <b><h:outputText value="#{discenteMonitoria.obj.projetoEnsino.titulo}" /></b>	</td>
			</tr>
	
			<tr>
				<th> Situação Monitoria: </th>
				<td> <b><h:outputText value="#{discenteMonitoria.obj.situacaoDiscenteMonitoria.descricao}" /></b>	</td>
			</tr>
			
			<tr>
				<th> Tipo Monitoria: </th>
				<td> <b><h:outputText value="#{discenteMonitoria.obj.tipoVinculo.descricao}" />	</b></td>
			</tr>
	

			<c:if test="${ not empty discenteMonitoria.orientacoesDiscenteMonitoriaLogado }">
				<tr>
					<td colspan="2">		
						<table style="listagem" width="100%">
							<caption class="listagem"> Orientações </caption>
							<thead>
							  <tr>
						       	<td>Orientador(a)</td>		
						       	<td>Inicío</td>					       	
						       	<td>Fim</td>					       				       	
						      </tr>
							</thead>
							<tbody>
								<c:forEach var="ori" varStatus="linha" items="${discenteMonitoria.orientacoesDiscenteMonitoriaLogado}" >
									<tr>
										<td>${ori.equipeDocente.servidor.siapeNome}</td>
										<td><fmt:formatDate pattern="dd/MM/yyyy" value="${ori.dataInicio}" /></td>
										<td><fmt:formatDate pattern="dd/MM/yyyy" value="${ori.dataFim}" /></td>						
									</tr>
								</c:forEach>
							</tbody>				
						</table>
						<br/>
						<br/>						
					</td>
				</tr>
			</c:if>
			
			
			<tr>
				<td colspan="2">		
						<table style="subFormulario" width="100%">
							<caption class="listagem"> Dados Bancários </caption>
							
							
							<tr>
								<td colspan="4">
									<div class="descricaoOperacao">
										<p>
											Não é permitido informar dados bancários de terceiros. Apenas uma conta bancária que tenha como titular o próprio aluno,
											 será aceita no cadastro para o recebimento de qualquer tipo de auxílio financeiro ou bolsa remunerada que o mesmo possa 
											 vir a ter na Universidade.
										</p>
									</div> 
								</td>
							</tr>
							<tr>
								<th> Banco:</th>
								<td>
									<h:selectOneMenu value="#{discenteMonitoria.obj.banco.id}" id="banco">
										<f:selectItem itemValue="-1" itemLabel="-- SELECIONE UM BANCO --"  />
										<f:selectItems value="#{discenteMonitoria.allBancoCombo}"/>
									</h:selectOneMenu>
								</td>
							</tr>
							
							<tr>
								<th> Nº Agência: </th>
								<td> <h:inputText value="#{discenteMonitoria.obj.agencia}" id="agencia"/>	</td>
							</tr>

							<tr>
								<th> Nº Conta Corrente: </th>
								<td> <h:inputText value="#{discenteMonitoria.obj.conta}" id="contaCorrente"/>	</td>
							</tr>
							
							<tr>
								<th> Nº de Operação: </th>
								<td> <h:inputText value="#{discenteMonitoria.obj.operacao}" id="operacao"/>	</td>
							</tr>
							
						</table>
				</td>
			</tr>
			
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">		
				</td>
			</tr>
		</tfoot>

	</table>
	
	
		<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
		<div align="center">		
				<h:commandButton id="btCadastrar" value="Atualizar Dados Bancários" action="#{discenteMonitoria.atualizarDadosBancarios}"/>
				<h:commandButton id="btCancelar" onclick="#{confirm}" value="Cancelar" action="#{discenteMonitoria.cancelar}"/>
		</div>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
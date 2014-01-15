<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%
	CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS} );
%>
<style>
.rich-progress-bar-width { width: 800px;}
.rich-progress-bar-uploaded-dig {font-size: 16px;}
.rich-progress-bar-shell-dig {font-size: 16px;}
</style>  
<f:view>
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
<a4j:keepAlive beanName="convocacaoVestibular"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Cadastrar Convocação de Candidatos do Vestibular</h2>
	<div class="descricaoOperacao">
	<p><b>Caro Usuário,</b></p>
	<p>Esta operação permite realizar a convocação dos alunos aprovados na
	ordem de chamada do vestibular desejada.</p>
	<p>Informe os valores no formulário, <b>clique
	uma única vez</b> em "Cadastrar Convocação", e aguarde até o fim
	do processamento.</p>
	</div>

	<h:form id="form">
	<a4j:keepAlive beanName="convocacaoVestibular"></a4j:keepAlive>
	<div align="center">
	<a4j:region id="progressPanel">
		<rich:progressBar id="progressBar" minValue="0" maxValue="100"
			value="#{ convocacaoVestibular.percentualProcessado }"
			label ="#{ convocacaoVestibular.mensagemProgresso }"
			reRenderAfterComplete="form">
			<f:facet name="initial">
				<a4j:outputPanel>
					<table class="formulario" width="80%">
						<caption>Dados da Convocação</caption>
						<tr>
							<th class="obrigatorio" width="25%">Processo Seletivo / Vestibular:</th>
							<td style="text-align: left;">
								<h:selectOneMenu id="selectPSVestibular" value="#{convocacaoVestibular.obj.processoSeletivo.id}">
									<f:selectItems id="listaAnosReferencia" value="#{convocacaoVestibular.processoSeletivoVestibularCombo}" />
								</h:selectOneMenu>
							</td>
						</tr>
						<tr>
							<th class="obrigatorio">Descrição da Convocação: </th>
							<td style="text-align: left;">
								<h:inputText id="descricao" value="#{convocacaoVestibular.obj.descricao}" size="60" maxlength="100"/>
								<ufrn:help>Informe uma descrição para esta convocação como, por exemplo: 1ª Chamada</ufrn:help>
							</td>
						</tr>
						<tr>
							<th class="obrigatorio">Data da Convocação:</th>
							<td style="text-align: left;">
								<t:inputCalendar value="#{convocacaoVestibular.obj.dataConvocacao}" 
									id="dataConvocacao" size="10" maxlength="10" 
								    onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
								    popupDateFormat="dd/MM/yyyy" 
								    renderAsPopup="true" renderPopupButtonAsImage="true" >
								      <f:converter converterId="convertData"/>
								</t:inputCalendar> 
							</td>
						</tr>
						<tfoot>
							<tr>
								<td colspan="2">
									<a4j:commandButton id="btnBuscar" value="Cadastrar Convocação" action="#{convocacaoVestibular.cadastrarConvocacoesImportacaoVestibular}" reRender="progressPanel" onclick="this.disabled=true; this.value='Por favor, aguarde...'"/>
									<h:commandButton value="Cancelar" action="#{ convocacaoVestibular.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
								</td>
							</tr>
						</tfoot>
					</table>
					<br/>
				</a4j:outputPanel>
			</f:facet>
			<f:facet name="complete">
	        	<br/>
	        	<h:outputText>
	        		<br/><br/>
	            	<table class="formulario" width="80%">
						<caption>
							<c:if test="${convocacaoVestibular.errosConvocacao == null}">Ocorreu um problema no processamento das notas</c:if>
	        				<c:if test="${convocacaoVestibular.errosConvocacao != null}">Processamento Concluído</c:if>
						</caption>
						<tr>
							<th class="rotulo" style="width: 250px;" >Processo Seletivo:</th>
							<td style="text-align: left;">
								<h:outputText value="#{convocacaoVestibular.obj.processoSeletivo.nome}"/>
							</td>
						</tr>
						<tr>
							<th class="rotulo">Descrição:</th>
							<td style="text-align: left;">
								<h:outputText value="#{convocacaoVestibular.obj.descricao}" />
							</td>
						</tr>
						<tr>
							<th class="rotulo">Data da Convocação:</th>
							<td style="text-align: left;">
								<h:outputText value="#{convocacaoVestibular.obj.dataConvocacao}"/>
							</td>
						</tr>
						<c:if test="${not empty convocacaoVestibular.resumoConvocacao}">
							<tr>
								<td colspan="2" style="text-align: left;" class="subFormulario">Resumo da Convocação</td>
							</tr>
							<tr>
								<td colspan="2">
									<table class="listagem">
									<thead>
										<tr>
											<th>Matriz Curricular</th>
											<th style="text-align: right;" width="10%">Convocados</th>
										</tr>
									</thead>
									<c:forEach items="#{convocacaoVestibular.resumoConvocacao}" var="item" varStatus="status">
										<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >
											<td>${item.key.descricao}</td>
											<td style="text-align: right;">${item.value}</td>
										</tr>
									</c:forEach>
									</table>
								</td>
							</tr>
						</c:if>
						<c:if test="${not empty convocacaoVestibular.errosConvocacao}">
						<tr>
							<td colspan="2">
								<table class="listagem" style="margin: 10px 0 0 0;">
									<caption>Candidatos que não foram Convocados por problemas de cadastro dos dados pessoais </caption>
									<thead>
										<tr>
											<th colspan="2">Matriz Curricular</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="#{convocacaoVestibular.errosConvocacao}" var="item" varStatus="status">
											<c:set var="erros" value="${item.value}" />
											<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >
												<td colspan="2" bgcolor="#C8D5EC" style="font-size: 0.9em; font-weight: bold;">${item.key.descricao}</td>
											</tr>	
											<tr class="matriz">
												<td class="tdCandidato" width="50%">Candidato</td>
												<td>Problema de Cadastro</td>
											</tr>
											<c:forEach items="#{item.value}" var="itemResult" varStatus="statusResult">
												<tr class="${statusResult.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
												<td class="candidato">${itemResult.pessoa.nome}</td>
												<td>
													<c:forEach items="#{itemResult.listaMensagens.mensagens}" var="itemMsg" varStatus="statusMsg">
														${itemMsg.mensagem}<br/>
													</c:forEach>
												</td>
												</tr>	
											</c:forEach>	
										</c:forEach>
									</tbody>
								</table>
							</td>
						</tr>
						</c:if>
						<tfoot>
							<tr>
								<td colspan="2">
									<h:commandButton value="Cancelar" action="#{ convocacaoVestibular.cancelar }" id="btnCancelar2" onclick="#{confirm}" immediate="true"/>
								</td>
							</tr>
						</tfoot>
					</table>
	            </h:outputText>
	        </f:facet>
		</rich:progressBar>
	</a4j:region>
</div>
</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
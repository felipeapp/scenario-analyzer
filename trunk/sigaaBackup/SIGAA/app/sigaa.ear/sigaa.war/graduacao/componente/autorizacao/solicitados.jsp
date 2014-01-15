<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<%@page import="br.ufrn.sigaa.ensino.dominio.ComponenteCurricular"%>

<f:view>
	<c:if test="${sessionScope.acesso.chefeDepartamento }">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</c:if>
	<%@include file="/graduacao/menu_coordenador.jsp"%>
	<%@include file="/stricto/menu_coordenador.jsp"%>
	<h2><ufrn:subSistema /> > Componentes Curriculares</h2>

	<div class="descricaoOperacao">
		Para validar ou editar Componentes Curriculares, digite um nome ou o código do componente, ou
		escolha as opções abaixo e clique em filtrar.<br />
		<b>Observação:</b> ao marcar os status desejados, será filtrados os componentes com pelo menos uma das situações marcadas.
	</div>

	<h:outputText value="#{autorizacaoComponente.create}" />
	<h:form id="formBusca">
		<table class="formulario" width="60%">
			<caption>Filtrar Componentes Curriculares</caption>
			<tbody>
				<tr>
					<td width="3%"><h:selectBooleanCheckbox styleClass="noborder" value="#{autorizacaoComponente.filtroCodigo}" id="checkCodigo"/></td>
					<th style="text-align:left;width:70px">Código:</th>
					<td><h:inputText size="10"
						id="filtroCodigo"
						value="#{autorizacaoComponente.codigo }"
						onfocus="$('formBusca:checkCodigo').checked = true;" onkeyup="CAPS(this)"/></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox styleClass="noborder" value="#{autorizacaoComponente.filtroNome}" id="checkNome" /></td>
					<th style="text-align:left;">Nome: </th>
					<td><h:inputText size="60"
						id="filtroNome"
						value="#{autorizacaoComponente.nome }"
						onfocus="$('formBusca:checkNome').checked = true;" maxlength="255" /></td>
				</tr>
				<tr>
					<td></td>
					<th style="text-align:left;">
						Situação:
					</th>
					<td>
						<h:selectBooleanCheckbox value="#{autorizacaoComponente.desativado}" id="checkDesativado" />Desativados &nbsp;&nbsp;&nbsp;
						<h:selectBooleanCheckbox value="#{autorizacaoComponente.aguardando}" id="checkAguardando" />Solicitados  &nbsp;&nbsp;&nbsp;
						<h:selectBooleanCheckbox value="#{autorizacaoComponente.negado}" id="checkNegado" />Negados					
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Filtrar" id="btnFiltrar"
						action="#{autorizacaoComponente.buscarComponentes}" /> <h:commandButton
						value="Cancelar" action="#{autorizacaoComponente.cancelar}" id="btnCancelar"
						onclick="#{confirm}" /></td>
				</tr>
			</tfoot>
		</table>
	
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena">	Campos de preenchimento obrigatório. </span> 
	</center>
	<br>
	<c:if test="${ busca and empty autorizacaoComponente.componentes }">
		<br>
		<div style="font-style: italic; text-align: center">Nenhum
		registro encontrado de acordo com os critérios de busca informados.</div>
	</c:if>

	<c:if test="${not empty  autorizacaoComponente.componentes}">
		<div class="infoAltRem">
			<h:graphicImage value="/img/view.gif" style="overflow: visible;" />:Detalhar Componente Curricular
			<c:if test="${autorizacaoComponente.roleCDP }">
				<h:graphicImage	value="/img/seta.gif" style="overflow: visible;" />:Selecionar Componente
			</c:if>
			<c:if test="${componenteCurricular.solicitacaoCadastroComponente}">
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />:Alterar Dados do Componente
			</c:if>
			<c:if test="${componenteCurricular.solicitacaoCadastroComponente}">
				<h:graphicImage value="/img/imprimir.gif" style="overflow: visible;" />:Imprimir Comprovante de Solicitação de Cadastro
			</c:if>
		</div>

		<table class="listagem">
			<caption class="listagem">Componentes Curriculares Encontrados (${fn:length(autorizacaoComponente.componentes)})</caption>

			<thead>
				<tr>
					<td width="8%">Código</td>
					<td>Nome</td>
					<td width="8%">Status</td>
					<td width="20%">Situação</td>
					<td width="2%"></td>
					<td width="2%"></td>
					<td width="2%"></td>
				</tr>
			</thead>

			<tbody>
				<c:forEach items="#{autorizacaoComponente.componentes}"
					var="componente" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }"> 
						<td>${componente.codigo}</td>
						<td>${componente.nome}</td>
						<td>${ componente.ativo? 'ATIVO' : 'INATIVO' }</td>
						<td>${componente.statusInativoDesc}</td>
						<td>
							<a href="javascript:void(0);" onclick="PainelComponente.show(${componente.id})" >
								<h:graphicImage value="/img/view.gif" title="Ver Detalhes do Componente Curricular" style="overflow: visible;" />
							</a>
						</td>
						<c:choose>
							<c:when test="${autorizacaoComponente.roleCDP }">
								<td>	
										<h:commandLink action="#{autorizacaoComponente.selecionaComponente}" style="border: 0;" >
											<h:graphicImage url="/img/seta.gif"/>
											<f:param name="id" value="#{componente.id}"/>
										</h:commandLink>
									
								</td>
								<td>										
										<h:commandLink id="alterar" action="#{componenteCurricular.atualizar}" style="border: 0;" >
											<h:graphicImage url="/img/alterar.gif"/>
											<f:param name="id" value="#{componente.id}"/>
										</h:commandLink>
								</td>
							</c:when>
						</c:choose>
						
						<c:choose>
							<c:when test="${componenteCurricular.solicitacaoCadastroComponente and (componente.foiNegado or componente.aguardandoConfirmacao)}">
								<td>						
									
									<h:commandLink  id="atualizar"	action="#{componenteCurricular.atualizar}" style="border: 0;" title="Solicitar alteração de dados" >
										<h:graphicImage url="/img/alterar.gif"/>
										<f:param name="id" value="#{componente.id}"/>
									</h:commandLink>							

								</td>
							</c:when>
						</c:choose>
					
						<c:choose>
							<c:when test="${componenteCurricular.solicitacaoCadastroComponente and componente.aguardandoConfirmacao}">
								<td>						
									
									<h:commandLink id="imprimir" action="#{componenteCurricular.reImprimirComprovante}" style="border: 0;"	title="Imprimir Comprovante de Solicitação de Cadastro" >										
										<h:graphicImage url="/img/imprimir.gif"/>
										<f:param name="id" value="#{componente.id}"/>										
									</h:commandLink>						
								</td>
							</c:when>
						</c:choose>
						
						<c:if test="${ componenteCurricular.solicitacaoCadastroComponente and !(componente.foiNegado or componente.aguardandoConfirmacao) }">
							<td></td><td></td>
						</c:if>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

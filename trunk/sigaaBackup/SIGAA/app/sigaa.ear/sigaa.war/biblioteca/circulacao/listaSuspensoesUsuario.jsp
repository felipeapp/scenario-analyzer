<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<f:view>

	<a4j:keepAlive beanName="suspensaoUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	
	<h:form id="formulario">

		<h2> <ufrn:subSistema /> &gt; Gerenciar Suspensões</h2>
		
		<div class="descricaoOperacao" style="width:85%;">
			<p>Utilize este formulário para gerenciar as suspensões de usuários da biblioteca.</p>
			<p>
				Para estornar uma ou mais suspensões, o motivo para o estorno deve ser informado.
				Selecione as suspensões a serem estornadas e clique em "Estornar Suspensões".
				Para confirmar a operação, sua senha deve ser digitada.
			</p>
			<p>O bibliotecário só tem a permissão de estornar suspensões ligadas a empréstimos
			feitos na biblioteca onde ele exerce suas funções.</p>
		</div>
		
		<%-- Exibe as informações do usuário --%>
		<c:set var="_infoUsuarioCirculacao" value="${suspensaoUsuarioBibliotecaMBean.informacaoUsuario}" scope="request"/>
		<c:set var="_mostrarVinculo" value="false" scope="request" />
		<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>
		<%--                                 --%>
		
		
		<div class="infoAltRem" style="width:90%;">
			<h:graphicImage value="/img/adicionar.gif" /> <h:commandLink action="#{suspensaoUsuarioBibliotecaMBean.preCadastrar}" value="Cadastrar Nova Suspensão" />
			<c:if test="${ not empty suspensaoUsuarioBibliotecaMBean.suspensoesUsuario}">
				<h:graphicImage value="/img/alterar.gif" />: Alterar Suspensão
			</c:if>
		</div>
		
		<table class="formulario" style="margin-bottom:10px;width:90%;">
			<caption>Suspensões Ativas (${fn:length(suspensaoUsuarioBibliotecaMBean.suspensoesUsuario)})</caption>
			
				<c:if test="${not empty suspensaoUsuarioBibliotecaMBean.suspensoesUsuario}">
					<thead>
						<tr>
							<th style="text-align:center;width:1%">&nbsp;</th>
							<th style="text-align:center; width:15%">Início</th>
							<th style="text-align:center; width:15%">Fim</th>
							<th style="width: 68%"></th>
							<th style="width: 1%;;"></th>
						</tr>
					</thead>
					
					<tbody>
						<c:forEach var="s" items="#{suspensaoUsuarioBibliotecaMBean.suspensoesUsuario}" varStatus="status">
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td  style="text-align:center;"><h:selectBooleanCheckbox styleClass="chk" value="#{s.selecionado}" /></td>
								<td style="text-align:center;"><ufrn:format type="data" valor="${s.dataInicio}" /></td>
								<td style="text-align:center;"><ufrn:format type="data" valor="${s.dataFim}" /></td>
								<td></td>
								<td>
									<h:commandLink action="#{suspensaoUsuarioBibliotecaMBean.preAlterar}" title="Alterar Suspensão">
										<h:graphicImage value="/img/alterar.gif" />
										<f:param name="id" value="#{s.id}" />
										<f:param name="suspensao_manual" value="#{s.manual}" />
									</h:commandLink>
								</td>
							</tr>
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<c:if test="${! s.manual}">
									<td colspan="3">Empréstimo que gerou suspensão: </td>
									<td colspan="2"> 
											Data do empréstimo: <ufrn:format type="dataHora" valor="${s.emprestimo.dataEmprestimo}" />  <br/>
											Prazo: <ufrn:format type="dataHora" valor="${s.emprestimo.prazo}" />  <br/>
											Data da devolução: <ufrn:format type="dataHora" valor="${s.emprestimo.dataDevolucao}" /> <br/>
											Código Barras: <h:outputText value="#{s.emprestimo.material.codigoBarras}" /> <br/>
											<h:outputText value="#{s.emprestimo.material.informacao}" />
									</td>
								</c:if>
								
								<c:if test="${s.manual}">
									<td colspan="3"> </td>
									<td colspan="2"> 
											<h:outputText value="#{s.emprestimo.material.informacao}" />
									</td>
								</c:if>
								
							</tr>
							
							<c:if test="${s.manual}" >
								<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
									<th colspan="3" style="font-style: italic;">Cadastrada por:</th>
									<td colspan="2" style="font-style: italic;"><h:outputText value="#{s.usuarioCadastro.nome}" /></td>
								</tr>
							</c:if>
							
						</c:forEach>
						
						<tr>
							<td colspan="5"><br/>
								<table class="subFormulario" width="100%">
									<caption>Estornar Suspensões Selecionadas</caption>
									<tr>
										<th style="vertical-align:top;width:150px;">Motivo:<span class="obrigatorio">&nbsp;</span></th>
										<td>
											<h:inputTextarea id="motivo" value="#{suspensaoUsuarioBibliotecaMBean.motivo}" rows="3"
													style="width:90%" onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 200);" />
										</td>
									</tr>
									<tr>
										<th>Caracteres Restantes:</th>
										<td>
											<span id="quantidadeCaracteresDigitados">200</span>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						
						<tr>
							<td colspan="5">
								<c:if test="${not empty suspensaoUsuarioBibliotecaMBean.suspensoesUsuario}">
									<c:set var="exibirApenasSenha" value="true" scope="request"/>
									<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
								</c:if>
							</td>
						</tr>
						
					</tbody>
				</c:if>
			
				<tfoot>
					<tr>
						<td colspan="5">
							<c:if test="${not empty suspensaoUsuarioBibliotecaMBean.suspensoesUsuario}">
							<h:commandButton value="Estornar Selecionadas" action="#{suspensaoUsuarioBibliotecaMBean.estornar}" id="acao" />
							</c:if>
							<h:commandButton value="<< Voltar" action="#{buscaUsuarioBibliotecaMBean.telaBuscaUsuarioBiblioteca}" id="voltar" />
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{suspensaoUsuarioBibliotecaMBean.cancelar}" immediate="true" id="cancelar" />
						</td>
					</tr>
				</tfoot>
			
				
				<c:if test="${empty suspensaoUsuarioBibliotecaMBean.suspensoesUsuario}">
					<tr><td style="text-align:center;font-weight:bold;color:green;">O usuário não possui suspensões ativas</td></tr>
				</c:if>
				
		</table>
		
		<c:if test="${not empty suspensaoUsuarioBibliotecaMBean.suspensoesUsuario}">
			<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
		</c:if>
		
	</h:form>
</f:view>

<script type="text/javascript" src="/shared/javascript/jquery/jquery.js"></script>

<script language="JavaScript">

var jQuery = jQuery.noConflict();

function marcarTodos(chk) {
	jQuery(".chk").attr("checked", chk.checked);
}


function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
	
	if (field.value.length > maxlimit){
		field.value = field.value.substring(0, maxlimit);
	}else{ 
		document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
	} 
}

</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
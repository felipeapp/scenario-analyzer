<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%-- CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.GESTOR_CONVOCACOES_VESTIBULAR} ); --%>
<style>
.rich-progress-bar-width { width: 800px;}
.rich-progress-bar-uploaded-dig {font-size: 16px;}
.rich-progress-bar-shell-dig {font-size: 16px;}
</style>  
<f:view>
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
<a4j:keepAlive beanName="convocacaoProcessoSeletivoTecnico"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Cadastrar Convoca��o de Candidatos</h2>
	<div class="descricaoOperacao">
	<p><b>Caro Usu�rio,</b></p>
	<p>Esta opera��o permite realizar a convoca��o dos alunos aprovados na
	ordem de chamada do processo seletivo.</p>
	<p>Informe os valores no formul�rio, <b>clique
	uma �nica vez</b> em "Cadastrar Convoca��o", e aguarde at� o fim
	do processamento.</p>
	<p>Caso seja a primeira convoc�o para o p�lo / grupo selecionado e deseje convocar todos os discentes aprovados e n�o-suplentes, marque a caixa "Todos Aprovados" e deixe as quantidades com o valor padr�o.</p>
	</div>

	<h:form>

<div align="center">
	<a4j:region id="progressPanel">
		<rich:progressBar interval="1000" id="progressBar" minValue="0" maxValue="100"
			value="#{ convocacaoProcessoSeletivoTecnico.percentualProcessado }"
			label ="#{ convocacaoProcessoSeletivoTecnico.mensagemProgresso }"
			reRenderAfterComplete="progressPanel" ignoreDupResponses="true">
			<f:facet name="initial">
				<a4j:outputPanel>
					<table class="formulario" width="80%">
						<caption>Dados da Convoca��o</caption>
						<tr>
							<th class="obrigatorio">Nome para a Convoca��o:</th>
							<td>
								<h:inputText id="descricao" value="#{convocacaoProcessoSeletivoTecnico.obj.descricao}" size="60" maxlength="100"/>
								<ufrn:help>Informe uma descri��o para esta convoca��o como, por exemplo: 1� Chamada</ufrn:help>
							</td>
						</tr>
						<tr>
							<th class="obrigatorio" width="25%">Processo Seletivo:</th>
							<td>
								<h:selectOneMenu id="selectPS" value="#{convocacaoProcessoSeletivoTecnico.obj.processoSeletivo.id}">
									<f:selectItems id="listaAnosReferencia" value="#{convocacaoProcessoSeletivoTecnico.processosCombo}" />
								</h:selectOneMenu>
							</td>
						</tr>
						<tr>
							<th>P�lo / Grupo:</th>
							<td>
								<h:selectOneMenu id="opcao" value="#{convocacaoProcessoSeletivoTecnico.obj.opcao.id}">
									<f:selectItem itemLabel="-- TODOS --" itemValue="0" />
									<f:selectItems value="#{ convocacaoProcessoSeletivoTecnico.polosCombo }" />
								</h:selectOneMenu>
							</td>
						</tr>
						<tr>
							<th>Convocar todos os aprovados:</th>
							<td>
								<h:selectBooleanCheckbox id="todosAprovados" value="#{convocacaoProcessoSeletivoTecnico.obj.todosAprovados }" /> 
								<ufrn:help>Marque esta caixa caso deseje convocar os candidatos aprovados e n�o SUPLENTES, para o p�lo / grupo selecionado. Deixe as quantidades com o valor padr�o para convocar todos os aprovados.</ufrn:help>
							</td>
						</tr>
						<tr>
							<th class="obrigatorio">Quantidade de discentes com Reserva de Vagas:</th>
							<td>
								<h:inputText id="qtdComReserva" value="#{convocacaoProcessoSeletivoTecnico.obj.quantidadeDiscentesComReserva}"  converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" size="4" maxlength="5" /> 
							</td>
						</tr>
						<tr>
							<th class="obrigatorio">Quantidade de discentes sem Reserva de Vagas:</th>
							<td>
								<h:inputText id="qtdSemReserva" value="#{convocacaoProcessoSeletivoTecnico.obj.quantidadeDiscentesSemReserva}" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" size="4" maxlength="5" /> 
							</td>
						</tr>
						<tfoot>
							<tr>
								<td colspan="2">
									<a4j:commandButton id="btnBuscar" value="Cadastrar Convoca��o" action="#{convocacaoProcessoSeletivoTecnico.cadastrarConvocacoesImportacaoTecnico}" reRender="progressPanel" onclick="this.disabled=true; this.value='Por favor, aguarde...'"/>
									<h:commandButton value="Cancelar" action="#{ convocacaoProcessoSeletivoTecnico.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
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
							<c:if test="${convocacaoProcessoSeletivoTecnico.errosConvocacao == null}">Ocorreu um problema no processamento das notas.</c:if>
	        				<c:if test="${convocacaoProcessoSeletivoTecnico.errosConvocacao != null}">Processamento Conclu�do.</c:if>
						</caption>
						<tr>
							<th class="rotulo" width="25%">Processo Seletivo / Vestibular:</th>
							<td>
								<h:outputText value="#{convocacaoProcessoSeletivoTecnico.obj.processoSeletivo.nome}"/>
							</td>
						</tr>
						<tr>
							<th class="rotulo">Descri��o:</th>
							<td>
								<h:outputText value="#{convocacaoProcessoSeletivoTecnico.obj.descricao}" />
							</td>
						</tr>
						<tr>
							<th class="rotulo">Data da Convoca��o:</th>
							<td>
								<h:outputText value="#{convocacaoProcessoSeletivoTecnico.obj.dataConvocacao}"/>
							</td>
						</tr>
						<tfoot>
							<tr>
								<td colspan="2">
									<h:commandButton value="Cancelar" action="#{ convocacaoProcessoSeletivoTecnico.cancelar }" id="btnCancelar2" onclick="#{confirm}" immediate="true"/>
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
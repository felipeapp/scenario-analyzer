<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<f:view>
	
	<a4j:keepAlive beanName="consultaDadosDiscentesIMD"></a4j:keepAlive>
	<h2><ufrn:subSistema /> &gt; Cadastros IMD &gt; Consultar Dados dos Discentes</h2>
	<div class="descricaoOperacao">
		<p>Esta operação permite consultar os dados cadastrais dos discentes do IMD.</p>
	</div>
	
	<h:form id="form">
	
		<table class="formulario" style="widt:43%;margin-bottom:10px;">
			<caption>Parâmetros da Busca</caption>
			
			<tr>
				<td style="width:20px;"><h:selectBooleanCheckbox value="#{consultaDadosDiscentesIMD.filtroNome}" id="checkNome" /></td>
				<td style="width:70px;"><label for="form:checkNome">Nome:</label></td>
				<td><h:inputText value="#{ consultaDadosDiscentesIMD.nomeBusca }" style="width:300px;" onfocus="$('form\:checkNome').checked = true;" /></td>
			</tr>
			
			<tr>
				<td><h:selectBooleanCheckbox value="#{consultaDadosDiscentesIMD.filtroMatricula}" id="checkMatricula" /></td>
				<td><label for="form:checkMatricula">Matrícula:</label></td>
				<td><h:inputText value="#{ consultaDadosDiscentesIMD.matriculaBusca }" onfocus="$('form\:checkMatricula').checked = true;" /></td>
			</tr>
			
			<tr>
				<td><h:selectBooleanCheckbox value="#{consultaDadosDiscentesIMD.filtroCpf}" id="checkCpf" /></td>
				<td><label for="form:checkCpf">Cpf:</label></td>
				<td><h:inputText value="#{ consultaDadosDiscentesIMD.cpfBusca }" size="14" maxlength="14" id="cpf" onblur="formataCPF(this, event, null)" onkeypress="return formataCPF(this, event, null)" onfocus="$('form\:checkCpf').checked = true;" /></td>
			</tr>
			
			<tr>
				<td><h:selectBooleanCheckbox value="#{consultaDadosDiscentesIMD.filtroOpcaoPoloGrupo}" id="checkOpcaoPoloGrupo" /></td>
				<td><label for="form:checkOpcaoPoloGrupo">Polo / Grupo:</label></td>
				<td><h:selectOneMenu value="#{ consultaDadosDiscentesIMD.opcaoPoloGrupoBusca }" id="opcaoPoloGrupo" onfocus="$('form\:checkOpcaoPoloGrupo').checked = true;">
						<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
						<f:selectItems value="#{ consultaDadosDiscentesIMD.opcoesPoloGrupo }" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td><h:selectBooleanCheckbox value="#{consultaDadosDiscentesIMD.filtroStatus}" id="checkStatus" /></td>
				<td><label for="form:checkStatus">Status:</label></td>
				<td><h:selectOneMenu value="#{ consultaDadosDiscentesIMD.statusBusca }" id="status" onfocus="$('form\:checkStatus').checked = true;">
						<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
						<f:selectItem itemValue="1" itemLabel="ATIVO" />
						<f:selectItem itemValue="2" itemLabel="CADASTRADO" />
						<f:selectItem itemValue="10" itemLabel="EXCLUÍDO" />
						<f:selectItem itemValue="13" itemLabel="PENDENTE CADASTRO" />
						<f:selectItem itemValue="15" itemLabel="PRÉ CADASTRADO" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton id="btnBuscar" value="Buscar Discentes" action="#{consultaDadosDiscentesIMD.buscar}"/>
						<h:commandButton value="Cancelar" action="#{ consultaDadosDiscentesIMD.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
		<c:if test="${ empty consultaDadosDiscentesIMD.discentes && (consultaDadosDiscentesIMD.filtroNome || consultaDadosDiscentesIMD.filtroMatricula || consultaDadosDiscentesIMD.filtroCpf) }">
			<div style="text-align:center;margin:10px;color:#FF0000;font-weight:bold;">Nenhum discente encontrado com os filtros informados.</div>
		</c:if>
		
		<c:if test="${ not empty consultaDadosDiscentesIMD.discentes }">
		
			<div class="infoAltRem" style="width:90%;"><h:graphicImage value="/img/seta.gif" alt="Selecionar Discente" />: Selecionar Discente</div>
		
			<table class='listagem' style="width:90%;">
				<caption>Discentes encontrados (${ fn:length(consultaDadosDiscentesIMD.discentes) })</caption>
				<thead>
					<tr><th style="text-align:center;">Matrícula</th><th>Nome</th><th>CPF</th><th>Status</th><th>Email</th><th style="text-align:center;">Turma</th><th style="width:20px;"></th></tr>
				</thead>
				<c:forEach items="#{ consultaDadosDiscentesIMD.discentes }" var="d" varStatus="indice">
					<tr class="${ indice.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td style="text-align:center;">${ d.matricula }</td>
						<td>${ d.pessoa.nome }</td>
						<td><ufrn:format type="cpf" valor="${ d.pessoa.cpf_cnpj }" /></td>
						<td>${ d.discente.statusString }</td>
						<td>${ d.pessoa.email }</td>
						<td style="text-align:center;">${ d.turmaEntradaTecnico.especializacao.descricao }</td>
						<td><h:commandLink action="#{ consultaDadosDiscentesIMD.visualizarDiscente }" title="Selecionar Discente"><f:param name="idDiscente" value="#{ d.id }" /><h:graphicImage value="/img/seta.gif" alt="Selecionar Discente" /></h:commandLink></td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
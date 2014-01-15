<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	<h2><ufrn:subSistema /> > Validar Foto 3x4</h2>

	<div class="descricaoOperacao">
		Utilize o formulário abaixo para buscar por fiscais e avaliar as fotos enviadas.<br/>
		No resultado da busca, selecione o novo status para foto do fiscal, de acordo com sua avaliação.<br/>
		Ao concluir a avaliação das fotos na página de resultados atual, clique no botão atualizar.  
	</div>
	
	<h:form id="form">
		<a4j:keepAlive beanName="validacaoFotoFiscalBean"></a4j:keepAlive>
		<table class="formulario" width="60%">
			<caption>Informe os Parâmetros</caption>
			<tr>
				<td width="5%">
					<h:selectBooleanCheckbox value="#{validacaoFotoFiscalBean.filtroNome}" id="filtroNome"/>
				</td>
				<td><label for="filtroNome" onclick="$('form:filtroNome').checked = !$('form:filtroNome').checked;">Nome:</label></td>
				<td>
					<h:inputText value="#{validacaoFotoFiscalBean.obj.pessoa.nome}" size="60" maxlength="60"  onfocus="$('form:filtroNome').checked = true;" id="nome"/>
				</td>
			</tr>
			<tr>
				<td width="5%">
					<h:selectBooleanCheckbox value="#{validacaoFotoFiscalBean.filtroCPF}" id="filtroCPF" />
				</td>
				<td><label for="filtroCPF" onclick="$('form:filtroCPF').checked = !$('form:filtroCPF').checked;">CPF:</label></td>
				<td>
					<h:inputText
						value="#{validacaoFotoFiscalBean.obj.pessoa.cpf_cnpj}" size="16" maxlength="14" id="txtCPF"
						onfocus="$('form:filtroCPF').checked = true;" 
						onkeypress="return formataCPF(this, event, null)">
						<f:converter converterId="convertCpf" />
					</h:inputText>
				</td>
			</tr>
			<tr>
				<td width="5%">
					<h:selectBooleanCheckbox value="#{validacaoFotoFiscalBean.filtroStatus}" id="filtroStatus"/>
				</td>
				<td><label for="filtroStatus" onclick="$('form:filtroStatus').checked = !$('form:filtroStatus').checked;">Status:</label></td>
				<td>
					<h:selectOneMenu id="statusFoto" 
						onchange="$('form:filtroStatus').checked = true;" 
						value="#{validacaoFotoFiscalBean.obj.statusFoto.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{statusFotoMBean.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="5%">
				</td>
				<td>Fotos por Página:</td>
				<td>
					<h:selectOneMenu id="tamanhoPagina" value="#{validacaoFotoFiscalBean.tamanhoPagina}">
						<f:selectItems value="#{validacaoFotoFiscalBean.tamanhoPaginaCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{validacaoFotoFiscalBean.buscar}" id="buscar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{validacaoFotoFiscalBean.cancelar}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br/>
	<c:if test="${not empty validacaoFotoFiscalBean.resultadosBusca}">
		<h:form id="formStatus">
			<br/>
			<table class="formulario" border="1" width="100%">
				<caption>Resultados da Busca (${fn:length(validacaoFotoFiscalBean.resultadosBusca)})</caption>
				<thead>
					<tr>
						<th style="text-align: center;">Foto</th>
						<th>Dados do Fiscal</th>
						<c:if test="${fn:length(validacaoFotoFiscalBean.resultadosBusca) > 1}">
							<th style="text-align: center;">Foto</th>
							<th>Dados do Fiscal</th>
						</c:if>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="#{validacaoFotoFiscalBean.resultadosBusca}" var="item" varStatus="status">
					<c:if test="${status.index % 2 == 0}">
						<tr class="${status.index % 4 == 0 ? "linhaPar" : "linhaImpar" }">
					</c:if>
					<td style="text-align: center;">
						<img src="${ctx}/verFoto?idArquivo=${item.idFoto}&key=${ sf:generateArquivoKey(item.idFoto) }" style="height: 120px"/>
					</td>
					<td style="text-align: right;">
						<table class="visualizacao" style="border: 0">
							<tr class="${status.index % 4 < 2 ? "linhaPar" : "linhaImpar" }">
								<th>CPF:</th>
								<td><h:outputText value="#{item.pessoa.cpf_cnpjString}"/></td>
							</tr>
							<tr class="${status.index % 4 < 2 ? "linhaPar" : "linhaImpar" }">
								<th>Nome:</th>
								<td>
									<h:outputText value="#{item.pessoa.nome}" />
								</td>
							</tr>
							<tr class="${status.index % 4 < 2 ? "linhaPar" : "linhaImpar" }">
								<th>Processo Seletivo:</th>
								<td>
									<h:outputText value="#{item.processoSeletivoVestibular.nome}" />
								</td>
							</tr>
							<tr class="${status.index % 4 < 2 ? "linhaPar" : "linhaImpar" }">
								<th valign="top">Status Atual:</th>
								<td>
									<h:outputText value="#{item.statusFoto.descricao}" />	
								</td>
							</tr>
							<tr class="${status.index % 4 < 2 ? "linhaPar" : "linhaImpar" }">
								<th valign="top">Novo Status:</th>
								<td>
									<h:selectOneMenu id="novoStatusFoto" value="#{item.novoStatusFoto.id}">
										<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
										<f:selectItems value="#{statusFotoMBean.allCombo}" />
									</h:selectOneMenu>
								</td>
							</tr>
						</table>
					</td>
					<c:if test="${status.index % 2 != 0}">
						</tr>
					</c:if>
				</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="4">
							<h:commandButton value="Atualizar Status" action="#{validacaoFotoFiscalBean.atualizar}" id="atualizarStatus" /> 
							<h:commandButton value="Cancelar" action="#{validacaoFotoFiscalBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />
						</td>
					</tr>
				</tfoot>
			</table>
		</h:form>
		<h:form id="formPaginacao">
			<br/>
			<div style="text-align: center;"> 
				<h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }" style="vertical-align:middle" id="paginacaoVoltar"/>
				<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true" id="mudaPagina">
					<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
				</h:selectOneMenu>
				<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}" style="vertical-align:middle" id="paginacaoAvancar"/>
				<br/><br/>
   				<em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
 			</div>
		</h:form>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
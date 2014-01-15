<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Validar/Invalidar Inscrição</h2>
	
	<h:form id="form">
		<a4j:keepAlive beanName="validacaoCandidatoBean"></a4j:keepAlive>
		<table class="formulario" width="60%">
			<caption>Informe os Parâmetros</caption>
			<tr>
				<td width="5%">
				</td>
				<td class="obrigatorio" width="25%">Processo Seletivo:</td>
				<td>
					<h:selectOneMenu id="processoSeletivo" 
						value="#{validacaoCandidatoBean.obj.processoSeletivo.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="5%">
					<h:selectBooleanCheckbox value="#{validacaoCandidatoBean.filtroNome}" id="filtroNome"/>
				</td>
				<td><label for="filtroNome" onclick="$('form:filtroNome').checked = !$('form:filtroNome').checked;">Nome:</label></td>
				<td>
					<h:inputText value="#{validacaoCandidatoBean.obj.pessoa.nome}" size="60" maxlength="60"  onfocus="$('form:filtroNome').checked = true;" id="nome"/>
				</td>
			</tr>
			<tr>
				<td width="5%">
					<h:selectBooleanCheckbox value="#{validacaoCandidatoBean.filtroCPF}" id="filtroCPF" />
				</td>
				<td><label for="filtroCPF" onclick="$('form:filtroCPF').checked = !$('form:filtroCPF').checked;">CPF:</label></td>
				<td>
					<h:inputText
						value="#{validacaoCandidatoBean.obj.pessoa.cpf_cnpj}" size="16" maxlength="14" id="txtCPF"
						onfocus="$('form:filtroCPF').checked = true;" 
						onkeypress="return formataCPF(this, event, null)">
						<f:converter converterId="convertCpf" />
					</h:inputText>
				</td>
			</tr>
			<tr>
				<td width="5%">
					<h:selectBooleanCheckbox value="#{validacaoCandidatoBean.filtroInscricao}" id="filtroInscricao"/>
				</td>
				<td><label for="filtroInscricao" onclick="$('form:filtroInscricao').checked = !$('form:filtroInscricao').checked;">Inscrição:</label></td>
				<td>
					<h:inputText value="#{validacaoCandidatoBean.obj.numeroInscricao}"  onfocus="$('form:filtroInscricao').checked = true;" 
						converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" id="numeroInscricao"
						maxlength="8" size="8"/>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{validacaoCandidatoBean.buscar}" id="cadastrar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{validacaoCandidatoBean.cancelar}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
		<br/>
		<c:if test="${not empty validacaoCandidatoBean.resultadosBusca}">
			<div class="infoAltRem">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Validar/Invalidar Inscrição
			</div>
			<table class="listagem">
				<caption>Resultados da Busca</caption>
				<thead>
					<tr>
						<th style="text-align: right;" width="5%">Ordem</th>
						<th style="text-align: center;" width="10%">Inscrição</th>
						<th style="text-align: center;" width="15%">CPF</th>
						<th style="text-align: left;">Nome</th>
						<th style="text-align: left;" width="15%">Status</th>
						<th width="5%"></th>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="#{validacaoCandidatoBean.resultadosBusca}" var="item" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td style="text-align: right;">${status.index + 1}</td>
						<td style="text-align: center;"><h:outputText value="#{item.numeroInscricao}"/></td>
						<td style="text-align: center;"><h:outputText value="#{item.pessoa.cpf_cnpjString}"/></td>
						<td style="text-align: left;">
							<h:outputText value="#{item.pessoa.nome}" />
						</td>
						<td>
							<h:outputText value="Validada" rendered="#{item.validada}"/>
							<h:outputText value="Pendente" rendered="#{not item.validada}"/>
						</td>
						<td>
							<h:commandLink title="Validar/Invalidar Inscrição"  action="#{validacaoCandidatoBean.atualizar}" style="border: 0;" id="atualizar">
								<f:param name="id" value="#{item.id}" id="idCandidato"/>
								<h:graphicImage url="/img/seta.gif" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h:messages />
	<a4j:keepAlive beanName="orientacaoAtividade"/>
	<f:subview id="menu">
		<c:if test="${orientacaoAtividade.portalDocente}">
			<%@include file="/portais/docente/menu_docente.jsp"%>
		</c:if>
		<c:if test="${orientacaoAtividade.portalCoordenadorGraduacao}">
			<%@include file="/graduacao/menu_coordenador.jsp"%>
		</c:if>	 
	</f:subview>
	<h2 class="title"><ufrn:subSistema /> > ${orientacaoAtividade.buscaRegistroAtivEspecificas ? 'Busca de Atividade Acadêmica': 'Consulta de Atividades Orientadas'}</h2>
	<c:if test="${acesso.dae}">
		<div class="descricaoOperacao">
			Permite a consulta e visualização
			${orientacaoAtividade.buscaRegistroAtivEspecificas ? ' dos registros de atividades acadêmicas':' das atividades orientadas'}. Para tanto, informe no formulário abaixo, os
			dados necessários para refinar a consulta.
			<c:if test="${orientacaoAtividade.escolheOrientador}">
				No campo <b>Orientador</b>, ao digitar o nome do docente aparecerá uma lista de sugestões. Selecione o docente desejado desta lista.
				<c:if test="${not acesso.dae}">
					A consulta às atividades serão limitadas à sua unidade.
				</c:if>
			</c:if>
		</div>
	</c:if>
	<c:if test="${not acesso.dae && acesso.ead}">
		<div class="descricaoOperacao">
			Permite a consulta e visualização
			${orientacaoAtividade.buscaRegistroAtivEspecificas ? ' dos registros de atividades acadêmicas':' das atividades orientadas'}. Para tanto, informe no formulário abaixo, os
			dados necessários para refinar a consulta.
		</div>
	</c:if>

	<h:form id="form">
	
	<h:inputHidden value="#{orientacaoAtividade.escolheOrientador}"/>
	<h:inputHidden value="#{orientacaoAtividade.orientador.id}"/>
	
		<table class="formulario" width="100%">
			<caption>Informe os Parâmetros da Busca</caption>
			<c:if test="${orientacaoAtividade.escolheOrientador}">
				<tr>
					<td><h:selectBooleanCheckbox value="#{orientacaoAtividade.filtroOrientador}" styleClass="noborder" id="checkOrientador"/></td>
					<td>Orientador:</td>
					<td>
						<c:if test="${acesso.dae}">
							<h:inputText value="#{orientacaoAtividade.orientador.pessoa.nome}" id="nomeServidor" size="60" onfocus="$('form:checkOrientador').checked = true;"/>
							<h:inputHidden value="#{orientacaoAtividade.orientador.id}" id="idServidor" />
			
							<ajax:autocomplete source="form:nomeServidor" target="form:idServidor"
								baseUrl="/sigaa/ajaxServidor" className="autocomplete"
								indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
								parser="new ResponseXmlToHtmlListParser()" />
							<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
						</c:if>
						<c:if test="${not acesso.dae && acesso.ead}">
							<h:selectOneMenu id="docenteEadCombo" value="#{orientacaoAtividade.orientador.id}" 
							 style="width: 600px;" onfocus="$('form:checkOrientador').checked = true;" >
							 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{orientacaoAtividade.docentesEad}"/>
							</h:selectOneMenu>
						</c:if>
						<c:if test="${not acesso.dae && !acesso.ead}">
							<h:selectOneMenu id="docenteCombo" value="#{orientacaoAtividade.orientador.id}" 
							 style="width: 600px;" onfocus="$('form:checkOrientador').checked = true;" >
							 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{orientacaoAtividade.docentes}"/>
							</h:selectOneMenu>
						</c:if>
					</td>
				</tr>
			</c:if>
			<c:if test="${not orientacaoAtividade.escolheOrientador}">
				<tr>
					<td></td>
					<td>Orientador:</td>
					<td>
						<h:outputText value="#{orientacaoAtividade.orientador.nome}"/>
					</td>
				</tr>
			</c:if>
			<tr>
				<td><h:selectBooleanCheckbox value="#{orientacaoAtividade.filtroDiscente}" styleClass="noborder" id="checkDiscente"/></td>
				<td>Discente:</td>
				<td>
					<h:inputText value="#{orientacaoAtividade.discente.pessoa.nome}" id="nomeDiscente" 
					style="width: 600px;" maxlength="120" size="60" onfocus="$('form:checkDiscente').checked = true;"/>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{orientacaoAtividade.filtroComponente}" styleClass="noborder" id="checkComponente"/></td>
				<td>Atividade:</td>
				<td>
					<h:selectOneMenu id="atividadeCombo" value="#{orientacaoAtividade.componenteCurricular.id}" 
					 style="width: 600px;" onfocus="$('form:checkComponente').checked = true;" >
					 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{orientacaoAtividade.atividadesCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{orientacaoAtividade.filtroAnoPeriodo}" styleClass="noborder" id="checkAnoPeriodo"/></td>
				<td>Ano-Período:</td>
				<td>
					<h:inputText value="#{ orientacaoAtividade.ano }" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" 
					converter="#{ intConverter }" onfocus="$('form:checkAnoPeriodo').checked = true;"/>.<h:inputText value="#{ orientacaoAtividade.periodo }" 
					size="1" maxlength="1" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" onfocus="$('form:checkAnoPeriodo').checked = true;"/></td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{orientacaoAtividade.filtroResultado}" styleClass="noborder" id="checkResultado"/></td>
				<td>Resultado:</td>
				<td>
					<h:selectManyCheckbox value="#{orientacaoAtividade.resultados}" layout="lineDirection"  id="selectResultado" onfocus="$('form:checkResultado').checked = true;">
						<f:selectItems value="#{orientacaoAtividade.resultadosCombo}"/>
					</h:selectManyCheckbox>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3">
						<input type="hidden" name="regAtividadesEspecificas" id="regAtividadesEspecificas" value="${orientacaoAtividade.buscaRegistroAtivEspecificas}" />
						<h:commandButton value="Buscar" action="#{orientacaoAtividade.buscar}" id="busca"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{orientacaoAtividade.cancelar}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	<br>
	
	<c:if test="${empty orientacaoAtividade.listaOrientacoesEncontradas}">
		<div align="center">Não foram encontradas orientações de atividades com os critérios informados.</div>
	</c:if>
	
	<c:if test="${not empty orientacaoAtividade.listaOrientacoesEncontradas}">
			<div class="infoAltRem" style="width: 100%">
			    <img src="/sigaa/img/view.gif" style="overflow: visible;"/>: Visualizar
			    <img src="/sigaa/img/email_go.png" style="overflow: visible;"/>: Enviar Mensagem
			</div>

			<table class="listagem">
				<caption>${orientacaoAtividade.buscaRegistroAtivEspecificas ? 'Registros':'Orientações'}  de Atividades Encontradas (${fn:length(orientacaoAtividade.listaOrientacoesEncontradas)})</caption>
				<thead>
				<tr>
					<td width="5%">Período</td>
					<td>Atividade</td>
					<c:if test="${orientacaoAtividade.escolheOrientador}">
						<td>Docente</td>
					</c:if>
					<td>Discente</td>
					<td width="10%">Status</td>
					<td width="5%"></td>
					<td width="5%"></td>
				</tr>
				</thead>
				<c:forEach var="item" items="#{orientacaoAtividade.listaOrientacoesEncontradas}" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${ item.registroAtividade.matricula.ano }.${ item.registroAtividade.matricula.periodo }</td>
						<td>${ item.registroAtividade.matricula.componenteCodigoNome }</td>
						<c:if test="${orientacaoAtividade.escolheOrientador}">
							<td>${ item.orientador.nome ne '' ? item.orientador.nome : item.orientadorExterno.nome }</td>
						</c:if>
						<td>${ item.registroAtividade.matricula.discente.nome}</td>
						<td>${item.registroAtividade.matricula.situacaoMatricula.descricao}</td>
						<td>
							<h:commandLink action="#{ orientacaoAtividade.visualizar }">
								<f:param name="id" value="#{ item.id }"/>
								<f:param name="idRegistroAtividade" value="#{ item.registroAtividade.id }"/>
								<f:verbatim><img src="/sigaa/img/view.gif" alt="Visualizar" title="Visualizar"/></f:verbatim>								
							</h:commandLink>
						</td>
						<td>
							<c:if test="${not empty item.registroAtividade.matricula.discente.usuario}">
								<a href="javascript://nop/" onclick="mensagem.show(<%=""+br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM%>, null, '${item.registroAtividade.matricula.discente.usuario.login}' );">
									<img src="/sigaa/img/email_go.png" alt="Enviar Mensagem" title="Enviar Mensagem"/>
								</a>
							</c:if>
							<c:if test="${empty item.registroAtividade.matricula.discente.usuario}">
								<img src="/sigaa/img/email_disabled.png" alt="Sem Caixa Postal" title="Usuário sem Caixa Postal"/>
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

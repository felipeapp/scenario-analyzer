<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<a4j:keepAlive beanName="orientacaoAtividadeNee"/>
	
	<h2 class="title"><ufrn:subSistema /> > ${orientacaoAtividadeNee.buscaRegistroAtivEspecificas ? 'Busca de Atividade Acadêmica': 'Consulta de Atividades Orientadas'}</h2>
	
	<div class="descricaoOperacao">
		Permite a consulta e visualização
		${orientacaoAtividadeNee.buscaRegistroAtivEspecificas ? ' dos registros de atividades acadêmicas':' das atividades orientadas'}. Para tanto, informe no formulário abaixo, os
		dados necessários para refinar a consulta.
	</div>

	<h:form id="form">
	
		<table class="formulario" width="100%">
			<caption>Informe os Parâmetros da Busca</caption>
			
			<tr>
				<td><h:selectBooleanCheckbox value="#{orientacaoAtividadeNee.filtroDiscente}" styleClass="noborder" id="checkDiscente"/></td>
				<td>Discente:</td>
				<td>
					<h:inputText value="#{orientacaoAtividadeNee.discente.pessoa.nome}" id="nomeDiscente" 
					style="width: 600px;" maxlength="120" size="60" onfocus="$('form:checkDiscente').checked = true;"/>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{orientacaoAtividadeNee.filtroNivelEnsino}" styleClass="noborder" id="checkNivelEnsino"/></td>
				<td>Nível de Ensino:</td>
				<td>
					<h:selectOneMenu id="nivelEnsinoCombo" value="#{orientacaoAtividadeNee.nivel}" 
					 style="width: 600px;" onfocus="$('form:checkNivelEnsino').checked = true;" 
					 valueChangeListener="#{orientacaoAtividadeNee.selecionarNivelEnsino}">
					 	<a4j:support event="onchange" reRender="atividadeCombo"/>
					 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{nivelEnsino.allCombo}"/>
					</h:selectOneMenu>
					
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{orientacaoAtividadeNee.filtroComponente}" styleClass="noborder" id="checkComponente"/></td>
				<td>Atividade:</td>
				<td>
					<h:selectOneMenu id="atividadeCombo" value="#{orientacaoAtividadeNee.componenteCurricular.id}" 
					 style="width: 600px;" onfocus="$('form:checkComponente').checked = true;" >
					 	<f:selectItem itemValue="0" itemLabel="#{orientacaoAtividadeNee.textoSelectItem}" />
						<f:selectItems value="#{orientacaoAtividadeNee.atividadesCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{orientacaoAtividadeNee.filtroAnoPeriodo}" styleClass="noborder" id="checkAnoPeriodo"/></td>
				<td>Ano-Período:</td>
				<td>
					<h:inputText value="#{ orientacaoAtividadeNee.ano }" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" 
					converter="#{ intConverter }" onfocus="$('form:checkAnoPeriodo').checked = true;"/>.<h:inputText value="#{ orientacaoAtividadeNee.periodo }" 
					size="1" maxlength="1" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" onfocus="$('form:checkAnoPeriodo').checked = true;"/></td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{orientacaoAtividadeNee.filtroResultado}" styleClass="noborder" id="checkResultado"/></td>
				<td>Resultado:</td>
				<td>
					<h:selectManyCheckbox value="#{orientacaoAtividadeNee.resultados}" layout="lineDirection"  id="selectResultado" onfocus="$('form:checkResultado').checked = true;">
						<f:selectItems value="#{orientacaoAtividadeNee.resultadosCombo}"/>
					</h:selectManyCheckbox>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3">
						<input type="hidden" name="regAtividadesEspecificas" id="regAtividadesEspecificas" value="${orientacaoAtividadeNee.buscaRegistroAtivEspecificas}" />
						<h:commandButton value="Buscar" action="#{orientacaoAtividadeNee.buscar}" id="busca"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{orientacaoAtividadeNee.cancelar}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	<br>
	
	<c:if test="${not empty orientacaoAtividadeNee.listaOrientacoesEncontradas}">
		<div class="infoAltRem" style="width: 100%">
		    <img src="/sigaa/img/view.gif" style="overflow: visible;"/>: Visualizar
		</div>

		<table class="listagem">
			<caption>${orientacaoAtividadeNee.buscaRegistroAtivEspecificas ? 'Registros':'Orientações'}  de Atividades Encontradas (${fn:length(orientacaoAtividadeNee.listaOrientacoesEncontradas)})</caption>
			<thead>
			<tr>
				<td width="5%">Período</td>
				<td>Atividade</td>
				<c:if test="${orientacaoAtividadeNee.escolheOrientador}">
					<td>Docente</td>
				</c:if>
				<td>Discente</td>
				<td width="10%">Status</td>
				<td width="10%">Situação NEE</td>
				<td width="2%"></td>
			</tr>
			</thead>
			<c:forEach var="item" items="#{orientacaoAtividadeNee.listaOrientacoesEncontradas}" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${ item.registroAtividade.matricula.ano }.${ item.registroAtividade.matricula.periodo }</td>
					<td>${ item.registroAtividade.matricula.componenteCodigoNome }</td>
					<c:if test="${orientacaoAtividadeNee.escolheOrientador}">
						<td>${ item.orientador.nome ne '' ? item.orientador.nome : item.orientadorExterno.nome }</td>
					</c:if>
					<td>${ item.registroAtividade.matricula.discente.nome }</td>
					<td>${ item.registroAtividade.matricula.situacaoMatricula.descricao }</td>
					<td>${ item.registroAtividade.matricula.discente.observacao }</td>
					<td>
						<h:commandLink action="#{ orientacaoAtividadeNee.visualizar }">
							<f:param name="id" value="#{ item.id }"/>
							<f:param name="idRegistroAtividade" value="#{ item.registroAtividade.id }"/>
							<f:verbatim><img src="/sigaa/img/view.gif" alt="Visualizar" title="Visualizar"/></f:verbatim>								
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

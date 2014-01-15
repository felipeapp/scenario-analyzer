<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script>
function checkTodos() {
	if ($('form:checkTodos').checked) {
		$('form:checkAno').checked = false;
		$('form:checkCoordenador').checked = false;
		$('form:checkCurso').checked = false;
		$('form:checkSituacao').checked = false;  
		$('form:checkAreaConhecimento').checked = false;
	}
}
</script>
<f:view>
<a4j:keepAlive beanName="buscaCursoLatoMBean" />
	<h2><ufrn:subSistema /> &gt; Consulta Proposta Curso Lato</h2>
		<h:form id="form">
  		 <table class="formulario" width="90%">
	  		<caption>Informe o critério de busca das Propostas Cursos </caption>
	   		  <tbody>

				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaCursoLatoMBean.filtroAno}" id="checkAno" styleClass="noborder" 
						onclick="if ($('form:checkAno').checked) $('form:checkTodos').checked = false;"/></td>
					<td>
						<label for="checkAno" onclick="$('form:checkAno').checked = !$('form:checkAno').checked;
							if ($('form:checkAno').checked) $('form:checkTodos').checked = false;">
							Ano:
						</label>
					</td>
					<td>
					    <h:inputText value="#{buscaCursoLatoMBean.ano}" id="anoProposta" size="5" maxlength="4" 
					    	 onkeyup="return formatarInteiro(this);" onfocus="$('form:checkAno').checked = true;"/>
					</td>
				</tr>		
			
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaCursoLatoMBean.filtroCoordenador}" id="checkCoordenador" styleClass="noborder"
						onclick="if ($('form:checkCoordenador').checked) $('form:checkTodos').checked = false;" /></td>
					<td>
						<label for="checkCoordenador" onclick="$('form:checkCoordenador').checked = !$('form:checkCoordenador').checked;
							if ($('form:checkCoordenador').checked) $('form:checkTodos').checked = false;">
							Coordenador/Vice:
						</label>
					</td>
					<td>
						<h:inputHidden id="idServidor" value="#{buscaCursoLatoMBean.servidor.id}"/>
						<h:inputText id="nomeServidor" value="#{buscaCursoLatoMBean.servidor.pessoa.nome}" size="50" onkeyup="CAPS(this)"
							disabled="#{buscaCursoLatoMBean.readOnly}" readonly="#{buscaCursoLatoMBean.readOnly}" 
							onfocus="$('form:checkCoordenador').checked = true;"/>
	
						<ajax:autocomplete source="form:nomeServidor" target="form:idServidor"
								baseUrl="/sigaa/ajaxDocente" className="autocomplete"
								indicator="indicatorDocente" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
								parser="new ResponseXmlToHtmlListParser()" />
	
						<span id="indicatorDocente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
					</td>
				</tr>

				<tr>
				<td><h:selectBooleanCheckbox value="#{buscaCursoLatoMBean.filtroCurso}" id="checkCurso" styleClass="noborder" 
					onclick="if ($('form:checkCurso').checked) $('form:checkTodos').checked = false;"/></td>
				<td>
					<label for="checkCurso" onclick="$('form:checkCurso').checked = !$('form:checkCurso').checked;
						if ($('form:checkCurso').checked) $('form:checkTodos').checked = false;">
						Curso:
					</label>
				</td>
				 <td>
				 	<h:inputHidden id="idCurso" value="#{buscaCursoLatoMBean.curso.id}"></h:inputHidden>
					<h:inputText id="nomeCurso"
							value="#{buscaCursoLatoMBean.curso.nome}" size="80" 
							onfocus="$('form:checkCurso').checked = true;"/> 
					<ajax:autocomplete source="form:nomeCurso" target="form:idCurso"
							baseUrl="/sigaa/ajaxCurso" className="autocomplete"
							indicator="indicatorCurso" minimumCharacters="3" parameters="nivel=L"
							parser="new ResponseXmlToHtmlListParser()" /> 
					<span id="indicatorCurso" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
				 </tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaCursoLatoMBean.filtroSituacao}" id="checkSituacao" styleClass="noborder"
						onclick="if ($('form:checkSituacao').checked) $('form:checkTodos').checked = false;" /></td>
					<td><label for="checkSituacao" onclick="$('form:checkSituacao').checked = !$('form:checkSituacao').checked;
						if ($('form:checkSituacao').checked) $('form:checkTodos').checked = false;">
						Situação da Proposta:
						</label></td>
					<td><h:selectOneMenu value="#{buscaCursoLatoMBean.situacaoProposta.id}" style="width:40%;" 
					onchange="$('form:checkSituacao').checked = true;" id="selectSituacaoProposta">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{situacaoPropostaMBean.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>

				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaCursoLatoMBean.filtroAreaConhecimento}" id="checkAreaConhecimento" styleClass="noborder" 
						onclick="if ($('form:checkAreaConhecimento').checked) $('form:checkTodos').checked = false;"/></td>
					<td><label for="checkAreaConhecimento" onclick="$('form:checkAreaConhecimento').checked = !$('form:checkAreaConhecimento').checked;
						if ($('form:checkAreaConhecimento').checked) $('form:checkTodos').checked = false;">Área Conhecimento:</label></td>
					<td><h:selectOneMenu value="#{buscaCursoLatoMBean.area.id}" style="width:40%;" 
							onfocus="$('form:checkAreaConhecimento').checked = true;
						if ($('form:checkAreaConhecimento').checked) $('form:checkTodos').checked = false;" id="selectAreaConhecimento">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{area.allGrandeAreas}" />
						</h:selectOneMenu>
					</td>
				</tr>

				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaCursoLatoMBean.filtroTodos}" id="checkTodos" styleClass="noborder" 
						onclick="checkTodos()" onchange="checkTodos()"/></td>
					<td>
						<label for="checkTodos" onclick="$('form:checkTodos').checked = !$('form:checkTodos').checked;checkTodos()" onchange="checkTodos()">Todos</label>
					</td>
				</tr>

			  </tbody>
			
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton action="#{buscaCursoLatoMBean.buscar}" value="Buscar" id="buttonBuscar" />
						<h:commandButton action="#{buscaCursoLatoMBean.cancelar}" value="Cancelar" onclick="#{confirm}" id="buttonCancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br />

		<c:if test="${not empty buscaCursoLatoMBean.listaCursoLato}">
			
			  <div class="infoAltRem">
			 	<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar dados da Proposta &nbsp;
	    		<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Proposta &nbsp;
	    		<h:graphicImage value="/img/trocar.gif" style="overflow: visible;"/>: Alterar Status da Proposta &nbsp; <br />
				<h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>: Ver Histórico &nbsp;
				<h:graphicImage value="/img/buscar.gif" style="overflow: visible;"/>: Visualizar Proposta &nbsp;	    	
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Cadastrar novo Curso &nbsp;
			  </div>

				<table class="listagem" id="lista-turmas">
		 		  <caption>Proposta Encontradas (${ fn:length(buscaCursoLatoMBean.listaCursoLato) })</caption>
					<thead>
						<tr>
						<td>Curso</td>
						<td>Coordenador</td>
						<td>Status</td>
						<td>Área</td>
						<td>Início</td>
						<td colspan="6"></td>
						</tr>
					</thead>
					<c:forEach items="#{buscaCursoLatoMBean.listaCursoLato}" var="item" varStatus="s">
						<tr class="${s.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>${item.nomeCurso }</td>
							<td>${item.coordenador }</td>
							<td>${item.situacao}</td>
							<td>${item.areaConhecimento}</td>
							<td><ufrn:format valor="${item.dataInicio}" type="data" /></td>
							<td width="10">
								<h:commandLink action="#{cursoLatoMBean.carregaObject}" >
									<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar Dados da Proposta"/>
									<f:param name="id" value="#{item.idCurso}"/>
								</h:commandLink>
							</td>
							<td width="10">
								<h:commandLink action="#{buscaCursoLatoMBean.remocao}" onclick="#{confirmDelete}">
									<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Proposta"/>
									<f:param name="id" value="#{item.idCurso}"/>
								</h:commandLink>
							</td>						
							<td width="10">
								<h:commandLink action="#{cursoLatoMBean.preAlteracaoSituacaoProposta}">
									<h:graphicImage value="/img/trocar.gif" style="overflow: visible;" title="Alterar Status da Proposta"/>
									<f:param name="id" value="#{item.idCurso}"/>
								</h:commandLink>
							</td>						
							<td width="10">
								<h:commandLink action="#{cursoLatoMBean.verHistorico}">
									<h:graphicImage value="/img/listar.gif" style="overflow: visible;" title="Ver Histórico"/>
									<f:param name="id" value="#{item.idCurso}"/>
								</h:commandLink>
							</td>
							<td width="10">
								<h:commandLink action="#{cursoLatoMBean.visualizar}" >
									<h:graphicImage value="/img/buscar.gif" style="overflow: visible;" title="Visualizar Proposta"/>
									<f:param name="id" value="#{item.idCurso}"/>
								</h:commandLink>
							</td>					
							<td width="10">
								<h:commandLink action="#{cursoLatoMBean.cadastrarCursoBaseadoEmCursoAntigo}" >
									<h:graphicImage value="/img/seta.gif" style="overflow: visible;" alt="Cadastrar novo Curso"/>
									<f:param name="id" value="#{item.idCurso}"/>
								</h:commandLink>
							</td>					
						</tr>
					</c:forEach>
				</table>
		</c:if>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
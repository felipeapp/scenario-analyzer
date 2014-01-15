<table class="formulario" style="width: 80%">
<caption> Informe os Critérios de Busca</caption>

	<c:if test="${not ofertaEstagioMBean.portalDiscente}">
		<tr>
			<td width="5px">
				<h:selectBooleanCheckbox id="checkEmpresa" value="#{ofertaEstagioMBean.filtroEmpresa}" styleClass="noborder" />
			</td>		
			<td width="150px">
				<label for="checkEmpresa" onclick="$('form:checkEmpresa').checked = !$('form:checkEmpresa').checked;">
					Concedente:
				</label>
			</td>				
			<td>
				<h:inputText id="empresa" value="#{ofertaEstagioMBean.empresa}" onfocus="$('form:checkEmpresa').checked = true;" onkeyup="CAPS(this);" size="60" maxlength="80"/>
			</td>
		</tr>
	</c:if>
	
	<tr>
		<td width="5px">
			<h:selectBooleanCheckbox id="checkTitulo" value="#{ofertaEstagioMBean.filtroTitulo}" styleClass="noborder" />
		</td>		
		<td width="150px">
			<label for="checkTitulo" onclick="$('form:checkTitulo').checked = !$('form:checkTitulo').checked;">
				Título:
			</label>
		</td>				
		<td>
			<h:inputText id="titulo" value="#{ofertaEstagioMBean.titulo}" onfocus="$('form:checkTitulo').checked = true;" onkeyup="CAPS(this);" size="60" maxlength="80"/>
		</td>
	</tr>	
	
	<tr>
		<td width="5px">
			<h:selectBooleanCheckbox id="checkPeriodo" value="#{ofertaEstagioMBean.filtroPeriodo}" styleClass="noborder" />
		</td>		
		<td width="150px">
			<label for="checkPeriodo" onclick="$('form:checkPeriodo').checked = !$('form:checkPeriodo').checked;">
				Período de Publicação:
			</label>
		</td>				
		<td>
			<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" onchange="Field.check('form:checkPeriodo')" title="Início da Publicação"
			renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return formataData(this,event)" value="#{ofertaEstagioMBean.dataInicio}" id="dataInicioPublicacao" /> 							
			a
			<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" onchange="Field.check('form:checkPeriodo')" title="Fim da Publicação"
			renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return formataData(this,event)" value="#{ofertaEstagioMBean.dataFim}" id="dataFimPublicacao" />							
		</td>			
	</tr>	
	
	
	<c:if test="${!ofertaEstagioMBean.selecionaCurso}">
		<tr>
			<td colspan="2" width="160px" style="text-align: right;"><b>Curso:</b></td>				
			<td>
				${ofertaEstagioMBean.cursoAtualCoordenacao.nome}
			</td>
		</tr>	
	</c:if>	 		 
	
	<c:if test="${ofertaEstagioMBean.portalDiscente}">
		<tr>
			<td colspan="2" width="160px" style="text-align: right;"><b>Curso:</b></td>				
			<td>
				${ofertaEstagioMBean.discenteUsuario.curso.descricao}
			</td>
		</tr>	
	</c:if>	 		
		
	<c:if test="${ofertaEstagioMBean.selecionaCurso && not ofertaEstagioMBean.portalDiscente}">
		<tr>
			<td width="5px">
				<h:selectBooleanCheckbox id="checkCurso" value="#{ofertaEstagioMBean.filtroCurso}" styleClass="noborder" />
			</td>		
			<td width="150px">
				<label for="checkCurso" onclick="$('form:checkCurso').checked = !$('form:checkCurso').checked;">
					Curso:
				</label>
			</td>				
			<td>
				<h:selectOneMenu id="curso" onfocus="$('form:checkCurso').checked = true;" value="#{ofertaEstagioMBean.curso.id}">
					<f:selectItem itemLabel="Todos" itemValue="0"/>
					<f:selectItems value="#{curso.allCursoGraduacaoCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
	</c:if>		
	
	<tr>
		<td colspan="3">
			<h:selectBooleanCheckbox id="checkVigentes" styleClass="noborder" value="#{ofertaEstagioMBean.exibirOfertasAbertas}"/>
			<label for="checkVigentes" onclick="$('form:checkVigentes').checked = !$('form:checkVigentes').checked;">
				Exibir Somente vigentes
			</label>			
		</td>
	</tr>		
	<tfoot>
	<tr>
		<td colspan="3">
			<h:commandButton id="btBuscar" action="#{ofertaEstagioMBean.buscar}" value="Buscar"/>
			<h:commandButton id="btCancelar" action="#{ofertaEstagioMBean.cancelar}" value="Cancelar" onclick="#{confirm}"/>
		</td>
	</tr>
	</tfoot>
</table>	
<br/>
<table class="formulario" style="width: 80%">
<caption> Informe os critérios de Busca</caption>
	 
	<tr>
		<td width="5px">
			<h:selectBooleanCheckbox id="checkDiscente" value="#{buscaEstagioMBean.filtroDiscente}" styleClass="noborder" />
		</td>		
		<td width="150px">
			<label for="checkDiscente" onclick="$('form:checkDiscente').checked = !$('form:checkDiscente').checked;">
				Discente:
			</label>
		</td>				
		<td>
			<h:inputText id="discente" value="#{buscaEstagioMBean.discente}" onfocus="$('form:checkDiscente').checked = true;" onkeyup="CAPS(this);" size="60" maxlength="80"/>
		</td>
	</tr>		 
	 
	<tr>
		<td width="5px">
			<h:selectBooleanCheckbox id="checkConcedente" value="#{buscaEstagioMBean.filtroConcedente}" styleClass="noborder" />
		</td>		
		<td width="150px">
			<label for="checkConcedente" onclick="$('form:checkConcedente').checked = !$('form:checkConcedente').checked;">
				Concedente do Estágio:
			</label>
		</td>				
		<td>
			<h:inputText id="concedente" value="#{buscaEstagioMBean.concedente}" onfocus="$('form:checkConcedente').checked = true;" onkeyup="CAPS(this);" size="60" maxlength="80"/>
		</td>
	</tr>	 
	
	<c:if test="${!buscaEstagioMBean.portalDocente}">	 		 	
		<tr>
			<td width="5px">
				<h:selectBooleanCheckbox id="checkOrientador" value="#{buscaEstagioMBean.filtroOrientador}" styleClass="noborder" />
			</td>		
			<td width="150px">
				<label for="checkOrientador" onclick="$('form:checkOrientador').checked = !$('form:checkOrientador').checked;">
					Orientador:
				</label>
			</td>				
			<td>
				<h:inputText id="orientador" value="#{buscaEstagioMBean.orientador}" onfocus="$('form:checkOrientador').checked = true;" onkeyup="CAPS(this);" size="60" maxlength="80"/>
			</td>
		</tr>			
	</c:if>
	<tr>
		<td width="5px">
			<h:selectBooleanCheckbox id="checkTipoEstagio" value="#{buscaEstagioMBean.filtroTipoEstagio}" styleClass="noborder" />
		</td>		
		<td width="150px">
			<label for="checkTipoEstagio" onclick="$('form:checkTipoEstagio').checked = !$('form:checkTipoEstagio').checked;">
				Tipo do Estágio:
			</label>
		</td>				
		<td>
			<h:selectOneMenu id="tipoEstagio" onfocus="$('form:checkTipoEstagio').checked = true;" value="#{buscaEstagioMBean.obj.tipoEstagio.id}">
				<f:selectItem itemLabel="Todos" itemValue="0"/>
				<f:selectItems value="#{estagioMBean.tipoEstagioCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tr>
		<td width="5px">
			<h:selectBooleanCheckbox id="checkPeriodo" value="#{buscaEstagioMBean.filtroPeriodo}" styleClass="noborder" />
		</td>		
		<td width="150px">
			<label for="checkPeriodo" onclick="$('form:checkPeriodo').checked = !$('form:checkPeriodo').checked;">
				Período do Estágio:
			</label>
		</td>				
		<td>
			<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" onchange="Field.check('form:checkPeriodo')"
			renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return formataData(this,event)" value="#{buscaEstagioMBean.obj.dataInicio}" id="dataInicio" /> 							
			a
			<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" onchange="Field.check('form:checkPeriodo')"
			renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return formataData(this,event)" value="#{buscaEstagioMBean.obj.dataFim}" id="dataFim" />							
		</td>			
	</tr>	
	
	<c:if test="${!buscaEstagioMBean.selecionaCurso}">
		<tr>
			<td colspan="2" width="160px" style="text-align: right;"><b>Curso:</b></td>				
			<td>
				${buscaEstagioMBean.cursoAtualCoordenacao.nome}
			</td>
		</tr>	
	</c:if>	 		 
	<c:if test="${buscaEstagioMBean.selecionaCurso}">
		<tr>
			<td width="5px">
				<h:selectBooleanCheckbox id="checkCurso" value="#{buscaEstagioMBean.filtroCurso}" styleClass="noborder" />
			</td>		
			<td width="150px">
				<label for="checkCurso" onclick="$('form:checkCurso').checked = !$('form:checkCurso').checked;">
					Curso:
				</label>
			</td>				
			<td>
				<h:selectOneMenu id="curso" onfocus="$('form:checkCurso').checked = true;" value="#{buscaEstagioMBean.curso.id}">
					<f:selectItem itemLabel="Todos" itemValue="0"/>
					<f:selectItems value="#{curso.allCursoGraduacaoCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
	</c:if>		
	
	<c:if test="${buscaEstagioMBean.portalDocente}">
		<tr>
			<td colspan="2" width="160px" style="text-align: right;"><b>Orientador:</b></td>				
			<td>
				${buscaEstagioMBean.servidorUsuario.nome}
			</td>
		</tr>	
	</c:if>		 		
	 			
	<tfoot>
	<tr>
		<td colspan="3">
			<h:commandButton id="btBuscar" action="#{buscaEstagioMBean.buscar}" value="Buscar"/>
			<h:commandButton id="btCancelar" action="#{buscaEstagioMBean.cancelar}" value="Cancelar" onclick="#{confirm}"/>
		</td>
	</tr>
	</tfoot>
</table>	
<br/>
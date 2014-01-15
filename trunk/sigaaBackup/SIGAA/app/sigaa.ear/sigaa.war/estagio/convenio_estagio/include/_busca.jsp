<table class="formulario" style="width: 60%">
<caption> Informe os critérios de Busca</caption>
	 
	<tr>
		<td width="5px">
			<h:selectBooleanCheckbox id="checkEmpresa" value="#{convenioEstagioMBean.filtroEmpresa}" styleClass="noborder" />
		</td>		
		<td style="width: 10%;">
			<label for="checkEmpresa" onclick="$('form:checkEmpresa').checked = !$('form:checkEmpresa').checked;">
				Concedente:
			</label>
		</td>				
		<td>
			<h:inputText id="empresa" value="#{convenioEstagioMBean.empresa}" onfocus="$('form:checkEmpresa').checked = true;" onkeyup="CAPS(this);" size="60" maxlength="80"/>
		</td>
	</tr>	 
	
	<c:if test="${ofertaEstagioMBean.portalConcedenteEstagio}">
		<tr>
			<th colspan="2" style="font-weight: bold;">Responsável:</th>
			<td>${convenioEstagioMBean.usuarioLogado.nome}</td>
		</tr>
	</c:if>
	
	<c:if test="${!ofertaEstagioMBean.portalConcedenteEstagio}">
		<tr>
			<td width="5px">
				<h:selectBooleanCheckbox id="checkResponsavel" value="#{convenioEstagioMBean.filtroResponsavel}" styleClass="noborder" />
			</td>		
			<td>
				<label for="checkResponsavel" onclick="$('form:checkResponsavel').checked = !$('form:checkResponsavel').checked;">
					Responsável:
				</label>
			</td>				
			<td>
				<h:inputText id="responsavel" value="#{convenioEstagioMBean.responsavel}" onfocus="$('form:checkResponsavel').checked = true;" onkeyup="CAPS(this);" size="60" maxlength="80"/>
			</td>
		</tr>
	</c:if>	
	
	<tr>
		<td width="5px">
			<h:selectBooleanCheckbox id="checkStatus" value="#{convenioEstagioMBean.filtroStatus}" styleClass="noborder"  
				rendered="#{ !convenioEstagioMBean.modoSeletor }" />
		</td>		
		<c:if test="${ convenioEstagioMBean.modoSeletor }">
			<th class="${ fn:length(convenioEstagioMBean.statusConvenioEstagioPreDefinidosCombo) == 1 ? 'rotulo' : 'obrigatorio'}">
				<label for="checkStatus" onclick="$('form:checkStatus').checked = !$('form:checkStatus').checked;">
					Situação:
				</label>
			</th>				
		</c:if>
		<c:if test="${ !convenioEstagioMBean.modoSeletor }">
			<td>
				<label for="checkStatus" onclick="$('form:checkStatus').checked = !$('form:checkStatus').checked;">
					Situação:
				</label>
			</td>				
		</c:if>
		<td>
			<h:selectOneMenu id="situacao" onfocus="$('form:checkStatus').checked = true;" value="#{convenioEstagioMBean.status.id}" 
				rendered="#{ !convenioEstagioMBean.modoSeletor }">
				<f:selectItem itemLabel="Todos" itemValue="0"/>
				<f:selectItems value="#{statusConvenioEstagioMBean.allCombo}"/>
			</h:selectOneMenu>
			<h:selectOneMenu id="situacaoSeletor" onfocus="$('form:checkStatus').checked = true;" value="#{convenioEstagioMBean.status.id}" 
				rendered="#{ convenioEstagioMBean.modoSeletor && fn:length(convenioEstagioMBean.statusConvenioEstagioPreDefinidosCombo) > 1 }">
				<f:selectItems value="#{convenioEstagioMBean.statusConvenioEstagioPreDefinidosCombo}"/>
			</h:selectOneMenu>
			<h:outputText value="#{ convenioEstagioMBean.status.descricao }" 
				rendered="#{ convenioEstagioMBean.modoSeletor && fn:length(convenioEstagioMBean.statusConvenioEstagioPreDefinidosCombo) == 1 }" />
		</td>
	</tr>	
	<tfoot>
	<tr>
		<td colspan="3">
			<h:commandButton id="btBuscar" action="#{convenioEstagioMBean.buscar}" value="Buscar"/>
			<h:commandButton id="btCancelar" action="#{convenioEstagioMBean.cancelar}" value="Cancelar" onclick="#{confirm}"/>
		</td>
	</tr>
	</tfoot>
</table>	
<br/>
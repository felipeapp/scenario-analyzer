
<script type="text/javascript">

var checkflag = "false";

function selectAllCheckBox() {
    var div = document.getElementById('form:listaProjetos');
    var e = div.getElementsByTagName("input");
   
    var i;

    if (checkflag == "false") {
            for ( i = 0; i < e.length ; i++) {
                    if (e[i].type == "checkbox"){ e[i].checked = true; }
            }
            checkflag = "true";
    } else {
            for ( i = 0; i < e.length ; i++) {
                    if (e[i].type == "checkbox"){ e[i].checked = false; }
            }
            checkflag = "false";
    }
}

</script>


	<h:form id="formBusca">

		<table class="formulario" width="90%" >
			<caption>Buscar Ações de Extensão</caption>
				<tbody>

			        <tr>
			            <td> <h:selectBooleanCheckbox value="#{filtroAtividades.checkTitulo}" id="selectBuscaTitulo" styleClass="noborder" /> </td>
			            <td> <label for="selectBuscaTitulo"> Título da Ação: </label> </td>
			            <td> <h:inputText id="buscaTitulo" value="#{filtroAtividades.buscaTitulo}" style="width:90%" onfocus="$('formBusca:selectBuscaTitulo').checked = true;" />
			            </td>
			        </tr>
				  
					<tr>
				    	<td> <h:selectBooleanCheckbox value="#{filtroAtividades.checkEdital}" styleClass="noborder" id="checkEdital"/></td>   	
						<td> <label for="checkEdital">Edital: </label></td>	
				    	<td>	    	
				    	 	<h:selectOneMenu id="buscaEdital" value="#{filtroAtividades.buscaEdital}" onfocus="javascript:$('formBusca:checkEdital').checked = true;">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue=""/>
					    	 	<f:selectItems value="#{editalExtensao.allCombo}" />
					    	 </h:selectOneMenu>	    	 
			    	 	</td>
			    	 </tr>	
			    	 
			    	 <tr>
						<td> <h:selectBooleanCheckbox value="#{filtroAtividades.checkAreaTematicaPrincipal}" id="checkAreaTematica" styleClass="noborder"/> </td>
				    	<td> <label for="unidade"> Área Temática:<c:if test="${ filtroAtividades.tipoFiltroDistribuicaoAutomatica }"><span class="required">&nbsp;</span></c:if> </label> </td>
				    	<td>
							<h:selectOneMenu id="buscaAreaTematica" value="#{filtroAtividades.buscaAreaTematicaPrincipal}" 
								onchange="javascript:$('formBusca:checkAreaTematica').checked = true;">
								<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --" />
								<f:selectItems value="#{areaTematica.allCombo}" />
							</h:selectOneMenu>
				    	</td>
				    </tr>
			    	 
			        <tr>
			            <td> <h:selectBooleanCheckbox value="#{filtroAtividades.checkAno}" id="selectBuscaAno" styleClass="noborder" /> </td>
			            <td> <label for="selectBuscaAno"> Ano: </label> </td>
			            <td> <h:inputText id="buscaAno" value="#{filtroAtividades.buscaAno}" maxlength="4" size="4" 
			                onkeyup="return formatarInteiro(this)" onchange="$('formBusca:selectBuscaAno').checked = true;" />
			            </td>
			        </tr>
			    	 
					 <tr>
				    	<td> <h:selectBooleanCheckbox value="#{filtroAtividades.checkFinanciamento}"	styleClass="noborder" id="checkFinanciamento"/>	</td>   	
						<td> <label for="titulo"> Financiamento: </label> </td>	
				    	<td>
					    	<h:selectBooleanCheckbox value="#{filtroAtividades.buscaFinanciamentoInterno}" 
						    	styleClass="noborder" onfocus="javascript:$('formBusca:checkFinanciamento').checked = true;"/> Financiamento Interno
							<br/>
					    	<h:selectBooleanCheckbox value="#{filtroAtividades.buscaFinanciamentoExterno}" 
						    	styleClass="noborder" onfocus="javascript:$('formBusca:checkFinanciamento').checked = true;"/> Financiamento Externo
							<br/>
					    	<h:selectBooleanCheckbox value="#{filtroAtividades.buscaAutoFinanciamento}" 
						    	styleClass="noborder" onfocus="javascript:$('formBusca:checkFinanciamento').checked = true;"/> Auto Financiamento
							<br/>
					    	<h:selectBooleanCheckbox value="#{filtroAtividades.buscaConvenioFunpec}" 
						    	styleClass="noborder" onfocus="javascript:$('formBusca:checkFinanciamento').checked = true;"/> Convênio Funpec
						</td>
					</tr>

					<c:if test="${ filtroAtividades.tipoFiltroAvaliacao || filtroAtividades.tipoFiltroDistribuicaoManual }">					
						<tr>
					    	<td> <h:selectBooleanCheckbox value="#{filtroAtividades.checkAguardandoAvaliacaoFinal}"	styleClass="noborder" id="checkAguardandoAvaliacao"/>	</td>   	
							<td> <label><b> Todas Aguardando Avaliação </b> </label> </td>
						</tr>	
					</c:if>
					
				</tbody>	
				<tfoot>
					<tr>						    	
						<td colspan="3">
							<h:commandButton value="Buscar" action="#{ filtroAtividades.filtrar }" id="btBuscar"/>
							<h:commandButton value="Cancelar" action="#{ filtroAtividades.cancelar }" 	id="btCancelar" onclick="#{confirm}" />							
						</td>
				   </tr>
			   </tfoot>
			</table>
	</h:form>
	<br/>
	<div class="descricaoOperacao">
		<p>
			Caro usuário(a),<br/><br/>
			
			A busca por 'Ano' considera o 'Intervalo de
			Execução' da Ação de Extensão e não o ano de cadastro da
			mesma no sistema. Por exemplo, quando é feita uma busca
			para o ano 2010, uma ação que iniciou em 2009 e prosseguiu
			até 2010 será mostrada na listagem.
			 
		</p>	
	</div>

 	<h:form id="form">

			<table class="formulario" width="90%">
			<caption>Busca por Discentes de Extensão</caption>
			<tbody>
				
				<tr>
					<td> <h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaTituloAtividade}" id="selectBuscaTitulo" styleClass="noborder"/> </td>
			    	<td> <label for="tituloAtividade"> Título da Ação </label> </td>
			    	<td> <h:inputText id="buscaTitulo" value="#{discenteExtensao.buscaTituloAtividade}" size="50" onfocus="javascript:$('form:selectBuscaTitulo').checked = true;"/></td>
			    </tr>
				
				<tr>
					<td><h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaAno}" id="selectBuscaAno" /></td>
			    	<td> <label for="anoAcao"> Ano da Ação </label> </td>
			    	<td> <h:inputText id="anoAcao" onkeyup="return formatarInteiro(this)" maxlength="4" value="#{discenteExtensao.anoReferencia}" 
			    				size="10" onfocus="javascript:$('form:selectBuscaAno').checked = true;" title="Ano da Ação"/></td>
			    </tr>
			    
			    <tr>
					<td><h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaPeriodoCadastro}" id="selectBuscaPeriodoCadastro" /></td>
			    	<td> <label for="inicio"> Período de Cadastro: </label> </td>
			    	<td>
						<t:inputCalendar
							onkeypress="return(formataData(this,event))"
							onchange="javascript:$('form:selectBuscaPeriodoCadastro').checked = true;"
							onfocus="javascript:$('form:selectBuscaPeriodoCadastro').checked = true;"
							size="10"
							value="#{discenteExtensao.dataInicioCadastro}"
							popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"							
							maxlength="10"
							id="inicioCadastro">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>	
						a
						<t:inputCalendar
							onkeypress="return(formataData(this,event))"
							onchange="javascript:$('form:selectBuscaPeriodoCadastro').checked = true;"
							onfocus="javascript:$('form:selectBuscaPeriodoCadastro').checked = true;"
							size="10"
							value="#{discenteExtensao.dataFimCadastro}"
							popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"							
							maxlength="10"
							id="fimCadastro">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						<ufrn:help img="/img/ajuda.gif">Período em que o bolsista foi cadastrado pela PROEX.</ufrn:help>
					</td>
			    </tr>
			    
			    <tr>
					<td><h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaAnoInicio}" id="selectBuscaAnoInicioFim" /></td>
			    	<td> <label for="inicio"> Plano de Trabalho: </label> </td>
			    	<td>
						<t:inputCalendar
							onkeypress="return(formataData(this,event))"
							onchange="javascript:$('form:selectBuscaAnoInicioFim').checked = true;"
							onfocus="javascript:$('form:selectBuscaAnoInicioFim').checked = true;"
							size="10"
							value="#{discenteExtensao.dataInicio}"
							popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"							
							maxlength="10"
							id="inicio">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>	
						a
						<t:inputCalendar
							onkeypress="return(formataData(this,event))"
							onchange="javascript:$('form:selectBuscaAnoInicioFim').checked = true;"
							onfocus="javascript:$('form:selectBuscaAnoInicioFim').checked = true;"
							size="10"
							value="#{discenteExtensao.dataFim}"
							popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"							
							maxlength="10"
							id="fim">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						<ufrn:help img="/img/ajuda.gif">Período de execução do plano de trabalho do discente.</ufrn:help>
					</td>
			    </tr>
			    
			    <tr>
					<td><h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaAnoInicioBolsa}" id="selectBuscaAnoInicioFimBolsa" /></td>
			    	<td> <label for="inicioBolsa"> Assumiu Bolsa: </label> </td>
			    	<td>
						<t:inputCalendar
							onkeypress="return(formataData(this,event))"
							onchange="javascript:$('form:selectBuscaAnoInicioFimBolsa').checked = true;"
							onfocus="javascript:$('form:selectBuscaAnoInicioFimBolsa').checked = true;"
							size="10"
							value="#{discenteExtensao.dataInicioBolsa}"
							popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"							
							maxlength="10"
							id="inicioBolsa">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>	
						a
						<t:inputCalendar
							onkeypress="return(formataData(this,event))"
							onchange="javascript:$('form:selectBuscaAnoInicioFimBolsa').checked = true;"
							onfocus="javascript:$('form:selectBuscaAnoInicioFimBolsa').checked = true;"
							size="10"
							value="#{discenteExtensao.dataFimBolsa}"
							popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"							
							maxlength="10"
							id="fimBolsa">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						<ufrn:help img="/img/ajuda.gif">Período em que o discente assumiu a bolsa na Ação de Extensão.</ufrn:help>						
					</td>
			    </tr>
			    
			    <tr>
					<td><h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaAnoInicioFinalizacao}" id="selectBuscaAnoInicioFimFinalizacao" /></td>
			    	<td> <label for="inicioFinalizacao"> Finalizou Bolsa: </label> </td>
			    	<td>
						<t:inputCalendar
							onkeypress="return(formataData(this,event))"
							onchange="javascript:$('form:selectBuscaAnoInicioFimFinalizacao').checked = true;"
							onfocus="javascript:$('form:selectBuscaAnoInicioFimFinalizacao').checked = true;"
							size="10"
							value="#{discenteExtensao.dataInicioFinalizacao}"
							popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"							
							maxlength="10"
							id="inicioFinalizacao">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>	
						a
						<t:inputCalendar
							onkeypress="return(formataData(this,event))"
							onchange="javascript:$('form:selectBuscaAnoInicioFimFinalizacao').checked = true;"
							onfocus="javascript:$('form:selectBuscaAnoInicioFimFinalizacao').checked = true;"
							size="10"
							value="#{discenteExtensao.dataFimFinalizacao}"
							popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"							
							maxlength="10"
							id="fimFinalizacao">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						<ufrn:help img="/img/ajuda.gif">Período em que o discente foi finalizado na Ação de Extensão.</ufrn:help>						
					</td>
			    </tr>

			    <tr>
					<td> <h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaEdital}" id="selectBuscaEdital" styleClass="noborder"/> </td>
			    	<td> <label for="edital"> Edital </label> </td>
			    	<td>	    	
			    	 <h:selectOneMenu id="buscaEdital" value="#{discenteExtensao.buscaEdital}" onfocus="javascript:$('form:selectBuscaEdital').checked = true;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
			    	 	<f:selectItems value="#{editalExtensao.allCombo}" />
			    	 </h:selectOneMenu>	    	 
			    	 </td>
			    </tr>
				    
				<tr>
					<td>
					<h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaDiscente}"  id="selectBuscaDiscente" />
					</td>
			    	<td> <label> Discente </label> </td>
					<td>
				
					 <h:inputText id="nomeDiscente" value="#{ discenteExtensao.discente.pessoa.nome }" size="60" onchange="javascript:$('form:selectBuscaDiscente').checked = true;"/>
					 <h:inputHidden id="idDiscente" value="#{ discenteExtensao.discente.id }"/>
				
					<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
						baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
						indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=ufrn"
						parser="new ResponseXmlToHtmlListParser()" />
					<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
					</td>
				</tr>
		
				<tr>
					<td>
					<h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaSituacao}"  id="selectBuscaSituacao" />
					</td>
			    	<td> <label> Situação </label> </td>
			    	<td>
		
			    	 <h:selectOneMenu value="#{discenteExtensao.situacaoDiscenteExtensao.id}" style="width: 200px"  onchange="javascript:$('form:selectBuscaSituacao').checked = true;">
			    	 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{discenteExtensao.allSituacaoDiscenteExtensaoCombo}"/>
		 			 </h:selectOneMenu>
		
			    	 </td>
			    </tr>
		
				<tr>
					<td>
					<h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaVinculo}"  id="selectBuscaVinculo" />
					</td>
			    	<td> <label> Vínculo </label> </td>
			    	<td>
				    	 <h:selectOneMenu value="#{discenteExtensao.vinculoDiscente.id}" style="width: 200px"  onchange="javascript:$('form:selectBuscaVinculo').checked = true;">
				    	 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{tipoVinculoDiscenteBean.allAtivosExtensaoCombo}"/>
			 			 </h:selectOneMenu>
			    	 </td>
			    </tr>
		
		
				<tr>
					<td>
					<h:selectBooleanCheckbox value="#{discenteExtensao.checkBuscaCurso}"  id="selectBuscaCurso" />
					</td>
			    	<td> <label> Curso do Discente </label> </td>
			    	<td>
		
			    	 <h:selectOneMenu value="#{discenteExtensao.curso.id}" style="width: 400px"  onfocus="javascript:$('form:selectBuscaCurso').checked = true;">
			    	 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{curso.allCursoGraduacaoCombo}"/>
		 			 </h:selectOneMenu>
		
			    	 </td>
			    </tr>
			    
			    <tr>
					<td>
					</td>
			    	<td> <label class="required"> Ordenar por: </label> </td>
			    	<td>
		
			    	 <h:selectOneRadio value="#{discenteExtensao.checkOrdenarDataCadastro}" style="width: 400px" >
						<f:selectItem itemValue="false" itemLabel="Ordem alfabética" />
						<f:selectItem itemValue="true" itemLabel="Período em que assumiu a bolsa" />
		 			 </h:selectOneRadio>
		
			    	 </td>
			    </tr>
			    
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
					<h:commandButton value="Buscar" action="#{ discenteExtensao.localizar }"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ discenteExtensao.cancelar }"/>
			    	</td>
			    </tr>
			</tfoot>
			</table>

	</h:form>
	
	<br/>

	<c:set var="discentes" value="#{discenteExtensao.discentesExtensao}"/>
	

	
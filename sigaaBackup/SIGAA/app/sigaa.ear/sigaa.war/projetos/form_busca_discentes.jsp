<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>


<div class="descricaoOperacao">
		<b>Caro(a) usuário(a),</b><br/><br/>
		
		A busca por 'Ano' considera o 'Intervalo de
		Execução' do Projeto e não o ano de cadastro do
		mesmo no sistema. Por exemplo, quando é feita uma busca
		para o ano 2010, um projeto que iniciou em 2009 e prosseguiu
		até 2010 será mostrado na listagem.
</div>

<h:form id="form" >

			<table class="formulario" width="90%">
			<caption>Busca por Discentes de Projetos</caption>
			<tbody>
				
				<tr>
					<td> <h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaTitulo}" id="selectBuscaTitulo" styleClass="noborder"/> </td>
			    	<td> <label for="titulo"> Título do Projeto: </label> </td>
			    	<td> <h:inputText id="buscaTitulo" value="#{discenteProjetoBean.buscaTitulo}" size="80" onfocus="javascript:$('form:selectBuscaTitulo').checked = true;"/></td>
			    </tr>
				
				<tr>
					<td><h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaAno}" id="selectBuscaAno" /></td>
			    	<td> <label for="ano"> Ano do Projeto: </label> </td>
			    	<td> <h:inputText id="ano" label="Ano do Projeto" onkeyup="return formatarInteiro(this);" maxlength="4" value="#{discenteProjetoBean.buscaAno}" size="10" onfocus="javascript:$('form:selectBuscaAno').checked = true;"/></td>
			    </tr>
			    
			    <tr>
					<td><h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaAnoInicioBolsa}" id="selectBuscaAnoInicioFimBolsa" /></td>
			    	<td> <label for="anoAcao"> Assumiu Bolsa: </label> </td>
			    	<td>
						<t:inputCalendar
							onkeypress="return(formataData(this,event))"
							onchange="javascript:$('form:selectBuscaAnoInicioFimBolsa').checked = true;"
							onfocus="javascript:$('form:selectBuscaAnoInicioFimBolsa').checked = true;"
							size="10"
							value="#{discenteProjetoBean.dataInicioBolsa}"
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
							value="#{discenteProjetoBean.dataFimBolsa}"
							popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"							
							maxlength="10"
							id="fimBolsa">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						<ufrn:help img="/img/ajuda.gif">Período em que o discente assumiu a bolsa no Projeto.</ufrn:help>						
					</td>
			    </tr>
			    
			    <tr>
					<td><h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaAnoInicioFinalizacao}" id="selectBuscaAnoInicioFimFinalizacao" /></td>
			    	<td> <label for="anoAcao"> Finaliza Bolsa: </label> </td>
			    	<td>
						<t:inputCalendar
							onkeypress="return(formataData(this,event))"
							onchange="javascript:$('form:selectBuscaAnoInicioFimFinalizacao').checked = true;"
							onfocus="javascript:$('form:selectBuscaAnoInicioFimFinalizacao').checked = true;"
							size="10"
							value="#{discenteProjetoBean.dataInicioFinalizacao}"
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
							value="#{discenteProjetoBean.dataFimFinalizacao}"
							popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"							
							maxlength="10"
							id="fimFinalizacao">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						<ufrn:help img="/img/ajuda.gif">Período em que o discente é finalizado no Projeto.</ufrn:help>						
					</td>
			    </tr>

			    <tr>
					<td> <h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaEdital}" id="selectBuscaEdital" styleClass="noborder"/> </td>
			    	<td> <label for="edital"> Edital: </label> </td>
			    	<td>	    	
			    	 <h:selectOneMenu id="buscaEdital" value="#{discenteProjetoBean.buscaEdital}" onfocus="javascript:$('form:selectBuscaEdital').checked = true;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
			    	 	<f:selectItems value="#{editalMBean.allCombo}" />
			    	 </h:selectOneMenu>	    	 
			    	</td>
			    </tr>
				    
				<tr>
					<td>
					<h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaDiscente}"  id="selectBuscaDiscente" />
					</td>
			    	<td> <label> Discente: </label> </td>
					<td>
				
					 <h:inputText id="nomeDiscente" value="#{ discenteProjetoBean.discente.pessoa.nome }" size="60" onchange="javascript:$('form:selectBuscaDiscente').checked = true;"/>
					 <h:inputHidden id="idDiscente" value="#{ discenteProjetoBean.discente.id }"/>
				
					<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
						baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
						indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=ufrn"
						parser="new ResponseXmlToHtmlListParser()" />
					<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
					</td>
				</tr>
				
				<tr>
					<td><h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaCoordenador}" id="selectBuscaCoordenador" styleClass="noborder" />
					</td>
					<td><label>Coordenador(a):</label></td>
					<td>
					<h:inputHidden id="buscaServidor" value="#{discenteProjetoBean.coordenador.id}" />
					<h:inputText id="buscaNome"	value="#{discenteProjetoBean.coordenador.pessoa.nome}" size="70" onfocus="javascript:$('form:selectBuscaCoordenador').checked = true;" /> 
						<ajax:autocomplete
							source="form:buscaNome" target="form:buscaServidor"
							baseUrl="/sigaa/ajaxServidor" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
							parser="new ResponseXmlToHtmlListParser()" /> 
						<span id="indicator" style="display:none;"> <img src="/sigaa/img/indicator.gif" /> </span>
					</td>
				</tr>
		
				<tr>
					<td>
					<h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaVinculo}"  id="selectBuscaVinculo" />
					</td>
			    	<td> <label> Vínculo </label> </td>
			    	<td>
				    	 <h:selectOneMenu value="#{discenteProjetoBean.vinculoDiscente.id}" style="width: 200px"  onchange="javascript:$('form:selectBuscaVinculo').checked = true;">
				    	 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{tipoVinculoDiscenteBean.allAtivosAssociadosCombo}"/>
			 			 </h:selectOneMenu>
			    	 </td>
			    </tr>
		
		
				<tr>
					<td>
					<h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaCurso}"  id="selectBuscaCurso" />
					</td>
			    	<td> <label> Curso do Discente </label> </td>
			    	<td>
		
			    	 <h:selectOneMenu value="#{discenteProjetoBean.curso.id}" style="width: 400px"  onfocus="javascript:$('form:selectBuscaCurso').checked = true;">
			    	 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{curso.allCursoGraduacaoCombo}"/>
		 			 </h:selectOneMenu>
		
			    	 </td>
			    </tr>
			    
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
					<h:commandButton value="Buscar" action="#{ discenteProjetoBean.buscar }"/>
					<h:commandButton value="Cancelar" action="#{ discenteProjetoBean.cancelar }" onclick="#{confirm}"/>
			    	</td>
			    </tr>
			</tfoot>
			</table>

</h:form>
<br/>

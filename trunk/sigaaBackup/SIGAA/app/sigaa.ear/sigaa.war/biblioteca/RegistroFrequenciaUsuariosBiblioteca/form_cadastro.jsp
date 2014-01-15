<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	.estiloMsgInformacao {
		color: green; 
		list-style-type: disc; 
		font-weight: bold;
	}
	
	.estiloMsgErro {
		color: red; 
		list-style-type: disc; 
		font-weight: bold;
	}
	
	.espacoColunasCabecalho {
		width: 205px;
		border: none;
	}
	
</style>


<f:view>

	<a4j:keepAlive beanName="registroFrequenciaUsuariosBibliotecaMBean" />
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Cadastrar movimentação de usuários </h2>
	
	<h:form id="form">

		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
			<div class="infoAltRem" style="margin-top: 10px">
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
				Remover Movimentações
			</div>
		</ufrn:checkRole>
		
		<table class="formulario" width="90%">
		<caption> Cadastrar Movimentação de usuários </caption>
			
			<tbody>
				<tr>
					<th class="required"> Biblioteca:</th>
					
					<td>	
						<h:selectOneMenu id="comBoxBibliotecasCadastroFrequenciaUsuario" value="#{registroFrequenciaUsuariosBibliotecaMBean.obj.biblioteca.id}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
							<f:selectItems value="#{registroFrequenciaUsuariosBibliotecaMBean.bibliotecasAtivasRegistroFrenquencia}"/>
						</h:selectOneMenu>
					</td>
				</tr>
			
				<tr>
					<th class="required">
						Turno:
					</th>
				
					<td align="left">
						<h:selectOneRadio value="#{registroFrequenciaUsuariosBibliotecaMBean.turnoSelecionado}" id="turnoSelecionado">
							<f:selectItem itemLabel="Matutino" itemValue="#{registroFrequenciaUsuariosBibliotecaMBean.TURNO_MATUTINO}" />
							<f:selectItem itemLabel="Vespertino" itemValue="#{registroFrequenciaUsuariosBibliotecaMBean.TURNO_VESPERTINO}" />
							<f:selectItem itemLabel="Noturno" itemValue="#{registroFrequenciaUsuariosBibliotecaMBean.TURNO_NOTURNO}" />
						</h:selectOneRadio>
					</td>
				</tr>
				
				<tr>
					<th class="required">
					Data de cadastro:
					</th>
					
					<td align="left" style="padding-right: 50px">
						<t:inputCalendar id="inptCldDataFim" value="#{registroFrequenciaUsuariosBibliotecaMBean.obj.dataCadastro}" 
						renderAsPopup="true" popupTodayString="Hoje:" popupWeekString="Semana"
						renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy"
						size="10" maxlength="10" 
						onkeypress="return formataData(this, event)"
						onblur="valida_data('form:inptCldDataFim');" />
					</td>
				</tr>
				<tr>
					<th class="required">
						Quantidade de usuários:
					</th>
					
					<td align="left">
						<h:inputText value="#{registroFrequenciaUsuariosBibliotecaMBean.obj.quantAcesso}" 
							size="5" maxlength="4" onkeypress="return ApenasNumeros(event);" />
					</td>
				</tr>
				
				
				<%--  Sub Tabela com a listagem das frequências de movimentações registradas  --%>
				
				<tr>
					<td colspan="2">
				
						<table class="subFormulario" width="100%">
							<caption> Frequências Registradas</caption>
				
							<c:if test="${empty registroFrequenciaUsuariosBibliotecaMBean.listaFrequenciaUsuariosBib}">
								<td style="text-align: center; font-style: italic; color: red; ">
									<h:outputText value="Não existem frequências registradas para os critérios da pesquisa"></h:outputText>
								</td>	
							</c:if>
				
							<c:if test="${not empty registroFrequenciaUsuariosBibliotecaMBean.listaFrequenciaUsuariosBib}">
				
									<td align="center">
										<h:dataTable columnClasses="espacoColunasCabecalho" 
													value="#{registroFrequenciaUsuariosBibliotecaMBean.listaFrequenciaUsuariosBib}" 
													var="item" binding="#{registroFrequenciaUsuariosBibliotecaMBean.dataTable}" >
													   
													  <h:column>
													    <f:facet name="header">
													      <h:outputText  value="Data"/>
													    </f:facet>
													     <h:outputText value="#{item.dataCadastro}" />
													  </h:column>
													  
													  <h:column>
													    <f:facet name="header">
													      <h:outputText  value="Turno"/>
													    </f:facet>
													     <h:outputText value="#{item.descricaoDoTurno}" />
													  </h:column>
													  
													  <h:column>
													    <f:facet name="header">
													      <h:outputText  value="Quantidade"/>
													    </f:facet>
													     <h:outputText value="#{item.quantAcesso}" />
													  </h:column>
													  
													  <h:column>
													    <f:facet name="header">
													      <h:outputText  value="Biblioteca"/>
													    </f:facet>
													     <h:outputText value="#{item.biblioteca.descricao}" />
													  </h:column>
													  
													  
													  <ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
														  <h:column>
														  	<f:facet name="header">
														  		<h:outputText value="Remover"/>
														  	</f:facet>
																	<h:commandLink action="#{registroFrequenciaUsuariosBibliotecaMBean.removerItem}"
																		onclick=" if(!confirm('Deseja realmente remover esse registro ?')) { return false; }">
																		<h:graphicImage value="/img/delete.gif" alt="Remover registro"/>
																		<f:param value="#{item.id}" name="idRegistroFrequenciaRemocao"></f:param>
																	</h:commandLink>
														  </h:column>
													  </ufrn:checkRole>
													  
										</h:dataTable> 
									</td>	
							</c:if>
				
							
						</table>
					
					</td>
				</tr>
				
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2">
							<h:commandButton action="#{registroFrequenciaUsuariosBibliotecaMBean.adicionarItemCadastro}" value="Cadastrar" />
							<h:commandButton value="<< Voltar" action="#{registroFrequenciaUsuariosBibliotecaMBean.iniciarRegistroFrequenciaUsuario}" />
							<h:commandButton value="Cancelar" action="#{registroFrequenciaUsuariosBibliotecaMBean.cancelar}" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
			
	</h:form>	
	<br/>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br/>
	<br/>
</center>

</f:view>

<script type="text/javascript">

// Essa funcao é usada porque como estou usando ajax, a mensagem de erro na validação 
// da data do JSF não estava aparecendo, entao estou validando também usando javascript
// apesar de não ser uma função padrão do sistema.

////////////////////////////////////////////////////////////////////////////////
////////																////////                
////////Criado por : Flavio Theruo Kaminisse                     		////////
////////email: falecomjaps@gmail.com                        		 	////////
////////url: http://www.japs.etc.br                         			////////
////////Data Criao : 30/08/2005                            				////////
////////																////////
////////Adaptado por: Jadson                          					////////
////////Data Modificação: 20/12/2007                      				////////
////////																////////
////////- Compativel com MSIE e Firefox.                     			////////
////////////////////////////////////////////////////////////////////////////////
//
//Funcao que valida a data
//chamada no evento *** onblur*** para funcionar
//
function valida_data(idCampoData) {

	var date = document.getElementById(idCampoData).value;
	
	if(date.length > 0){ // so valida se tiver algo
	
		var array_data = new Array;
		
		//                                  dia          /     mes       /    ano
		var ExpReg = new RegExp("(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/[12][0-9]{3}");
		
		//vetor que contem o dia o mes e o ano
		array_data = date.split("/");
		erro = false;
		
		//Valido se a data esta no formato dd/mm/yyyy e se o dia tem 2 digitos e esta entre 01 e 31
		//se o mes tem d2 digitos e esta entre 01 e 12 e o ano se tem 4 digitos e esta entre 1000 e 2999
		if ( date.search(ExpReg) == -1 )
			erro = true;
		
		//Valido os meses que nao tem 31 dias com execao de fevereiro
		else if ( ( ( array_data[1] == 4 ) || ( array_data[1] == 6 ) || ( array_data[1] == 9 ) || ( array_data[1] == 11 ) ) && ( array_data[0] > 30 ) )
			erro = true;
		
		//Valido o mes de fevereiro
		else if ( array_data[1] == 2 ) {
				//Valido ano que nao e bissexto
				if ( ( array_data[0] > 28 ) && ( ( array_data[2] % 4 ) != 0 ) )
					erro = true;
				//Valido ano bissexto
				if ( ( array_data[0] > 29 ) && ( ( array_data[2] % 4 ) == 0 ) )
					erro = true;
			}
			if ( erro ) {
				alert("Data Inválida");
				document.getElementById(idCampoData).value = '';
				document.getElementById(idCampoData).focus(); // nao funciona no firefox
			}
	}	
}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
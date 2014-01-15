<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<f:verbatim>
	<script type="text/javascript">
		function esconder(obj) {

				if (obj.value == 'servidor'){
					document.getElementById( 'aluno' ).style.display = "none";
					document.getElementById( 'servidor' ).style.display = "";
				}

				if (obj.value == 'aluno'){
					document.getElementById( 'aluno' ).style.display = "";
					document.getElementById( 'servidor' ).style.display = "none";
				}
			
		}
		
	</script>
</f:verbatim>




<h:messages showDetail="true"></h:messages>
<h2><ufrn:subSistema /> > Dados da Prestação de Serviços</h2>
<br>

<h:form id="equipe"> 
<table class=formulario width="95%" id="tb1">

	<caption class="listagem">Dados da Equipe da Prestação de Serviços</caption>


	<tr>
		<td colspan="3">
			<b>Tipo de Membro da Equipe:</b><br/>
			
				<h:selectOneRadio value="#{prestacaoServico.tipoMembro}" onclick="javascript:esconder(this);">
				  	<f:selectItem itemLabel="Servidores" itemValue="servidor" />
				  	<f:selectItem itemLabel="Alunos" itemValue="aluno" />
				</h:selectOneRadio>
			
		</td>
	</tr>



	<tr>
		<td colspan="3">

			<table width="100%" id="servidor">
				<caption class="listagem">Professores e Funcionários</caption>
	
				<tr>
					<td colspan="3">
						<b>Nome: </b><br/>
						<h:inputHidden id="idServidor" value="#{prestacaoServico.membroEquipePrestacao.servidor.id}"></h:inputHidden>
						<h:inputText id="nomeServidor"	value="#{prestacaoServico.membroEquipePrestacao.servidor.pessoa.nome}" size="80" />
						
						 <ajax:autocomplete
							source="equipe:nomeServidor" target="equipe:idServidor"
							baseUrl="/sigaa/ajaxServidor" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
							parser="new ResponseXmlToHtmlListParser()" /> 
							<span id="indicator" style="display:none;"> <img src="/sigaa/img/indicator.gif" /> 	</span>
							<h:selectBooleanCheckbox value="#{prestacaoServico.coordenador}" /> Coordenador(a)
						<br/>


					</td>
				</tr>

			</table>
			
			
			<table width="100%" id="aluno" style="display: none">
				<caption class="listagem">Alunos da ${ configSistema['siglaInstituicao'] }</caption>
	
				<tr>
					<td colspan="3">
					<b>Nome: </b><br/>

					<h:inputHidden id="idDisc" value="#{prestacaoServico.membroEquipePrestacao.discente.id}"></h:inputHidden>
					<h:inputText id="discente"	value="#{prestacaoServico.membroEquipePrestacao.discente.pessoa.nome}" size="80" />
					
					 <ajax:autocomplete
						source="equipe:discente" target="equipe:idDisc"
						baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
						indicator="indicator" minimumCharacters="3" parameters="tipo=nome,nivel=ufrn"
						parser="new ResponseXmlToHtmlListParser()" /> 
						<span id="indicator" style="display:none;"> <img src="/sigaa/img/indicator.gif" /> 	</span>
					</td>
				</tr>
				
				
			</table>

		</td>
	</tr>


	<tr>
		<td>
			<b>Categoria Funcional:</b><br/>					
			<h:selectOneMenu value="#{prestacaoServico.membroEquipePrestacao.categoriaFuncional}">
			  	<f:selectItem itemLabel="> Opções" 				itemValue="0" />
			  	<f:selectItem itemLabel="Docente" 				itemValue="1" />
			  	<f:selectItem itemLabel="Tec. Nível Médio" 		itemValue="2" />
			  	<f:selectItem itemLabel="Tec. Nível Superior" 	itemValue="3" />
			  	<f:selectItem itemLabel="Aluno Voluntário"		itemValue="4" />
			  	<f:selectItem itemLabel="Aluno Bolsista" 		itemValue="5" />
			</h:selectOneMenu>
		</td>	
		
		<td>
			<b>Fone/Fax:</b><br/>					
			<h:inputText value="#{prestacaoServico.membroEquipePrestacao.foneFax}" size="20" maxlength="15"/>
		</td>	
		
		<td>
			<b>C/H Semanal:</b><br/>					
			<h:inputText value="#{prestacaoServico.membroEquipePrestacao.chSemanal}" size="5" maxlength="4"/>
		</td>					
	</tr>
				
	<tr>
		<td colspan="3">
			<h:commandButton value="Adicionar" action="#{prestacaoServico.adicionarMembro}" />			
		</td>
	</tr>


	<tr>
		<td colspan="3"><b>Lista de Membros da Equipe</b><hr/></td>
	</tr>


	
	<input type="hidden" name="idPessoa" id="idPessoa" />

	<tr>
		<td colspan="3">
				
				
					<t:dataTable value="#{prestacaoServico.obj.membroPrestacaoServicoCollection}" var="mP" align="center" width="100%" id="comp">
						<t:column>
							<f:facet name="header">
								<f:verbatim>Nome</f:verbatim>
							</f:facet>
							<h:outputText value="#{mP.pessoa.nome}" />
							<f:verbatim>
								<h:outputText value="  (Coordenador(a))" rendered="#{mP.tipo == 2}"/> <br/>
							</f:verbatim>
						 </t:column>	

						<t:column>
							<f:facet name="header">
								<f:verbatim>Categoria Funcional</f:verbatim>
							</f:facet>
							<h:outputText value="#{mP.categoriaFuncional}" />
						</t:column>	

						<t:column>
							<f:facet name="header">
								<f:verbatim>C/H Semanal</f:verbatim>
							</f:facet>
							<h:outputText value="#{mP.chSemanal}" />
						</t:column>	


						<t:column>
							<f:facet name="header">
								<f:verbatim>Fone/Fax</f:verbatim>
							</f:facet>
							<h:outputText value="#{mP.foneFax}" />
						</t:column>	
						  
						<t:column width="5%" styleClass="centerAlign">					
							<f:facet name="header"><f:verbatim></f:verbatim></f:facet>										
							<h:commandButton image="/img/delete.gif" action="#{prestacaoServico.removeMembro}" alt="Remover Membro da Equipe" onclick="$(idPessoa).value=#{mP.pessoa.id}"/>					
						</t:column>
							  
						  
					</t:dataTable>
		</td>
	</tr>
	


	<tfoot>
		<tr> 
			<td colspan="3">
				<h:commandButton value="<< Voltar" action="#{prestacaoServico.irTelaContratante}" />
				<h:commandButton value="Cancelar" action="#{prestacaoServico.cancelar}" />
				<h:commandButton value="Avançar >>" action="#{prestacaoServico.irTelaServico}" />
			</td> 
		</tr>
	</tfoot>

	</table>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
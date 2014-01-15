<%@include file="/public/include/cabecalho.jsp" %>

<h2> Assinatura Digital do Grupo de Pesquisa </h2>

<style>
	tr.negrito th {font-weight: bold;}
	tr.espacamento td {padding: 25px 2px 2px;}
</style>

<f:view>
  <h:form id="form">
	<table class="listagem" width="80%">
		<caption>Informa��es Grupo de Pesquisa</caption>
		
		<tr>
			<td colspan="2" class="subFormulario">Dados Gerais</td>
		</tr>

		<tr class="negrito">
			<th width="20%;"> T�tulo Grupo Pesquisa: </th>
			<td> <h:outputText value="#{ propostaGrupoPesquisaMBean.obj.nome }" /></td>
		</tr>

		<tr class="negrito">
			<th> L�der: </th>
			<td> <h:outputText value="#{ propostaGrupoPesquisaMBean.obj.coordenador.nome }" /></td>
		</tr>
		
		<tr class="negrito">
			<th> Vice-L�der: </th>
			<td> <h:outputText value="#{ propostaGrupoPesquisaMBean.obj.viceCoordenador.pessoa.nome }" /></td>
		</tr>
		
		<tr class="negrito">
			<th> �rea de Conhecimento: </th>
			<td> <h:outputText value="#{ propostaGrupoPesquisaMBean.area.nome }" /></td>
		</tr>
		
		<tr class="negrito">
			<th> Sub-�rea de Conhecimento: </th>
			<td> <h:outputText value="#{ propostaGrupoPesquisaMBean.obj.areaConhecimentoCnpq.nome }" /></td>
		</tr>

        <c:if test="${ not empty propostaGrupoPesquisaMBean.obj.linhasPesquisaCol }">
	        <tr>
	        	<td colspan="2" class="subFormulario"> Linhas de Pesquisa </td>
	        </tr>
	        <tr>
	        	<td colspan="2">
					<rich:dataTable id="gp" value="#{ propostaGrupoPesquisaMBean.obj.linhasPesquisaCol }" 
						var="linhas" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
	        			<rich:column>
							<f:facet name="header"><f:verbatim>Linha de Pesquisa</f:verbatim></f:facet>
							<h:outputText value="#{linhas.nome}"/>
	        			</rich:column>
	        		</rich:dataTable>
	        	</td>
	        </tr>
        </c:if>

		<tr>
			<td colspan="2" style="padding: 10px 2px 2px;"></td>
		</tr>

		<!-- Membros -->
		<a4j:region rendered="#{ not empty propostaGrupoPesquisaMBean.obj.membrosPermanentes }">
			<tr>
				<td colspan="2" class="subFormulario">Membros Permanentes</td>
			</tr>
			<tr>
				<td colspan="2">
					<rich:dataTable id="dtPermanentes" value="#{propostaGrupoPesquisaMBean.obj.membrosPermanentes}" 
						var="membro" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar"
						binding="#{propostaGrupoPesquisaMBean.membrosPermanentes}">
							<rich:column>
								<f:facet name="header"><h:outputText value="Pesquisador" /></f:facet>
								<h:outputText value="#{membro.pessoa.nome}"/>
							</rich:column>
					</rich:dataTable>
				</td>
			</tr>
		</a4j:region>

		<a4j:region rendered="#{ not empty propostaGrupoPesquisaMBean.obj.membrosAssociados }">
			<tr>
				<td colspan="2" class="subFormulario">Membros Associados</td>
			</tr>
			<tr>
				<td colspan="2">
					<rich:dataTable id="dtAssociados" value="#{propostaGrupoPesquisaMBean.obj.membrosAssociados}" 
						var="membro" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar"
						binding="#{propostaGrupoPesquisaMBean.membrosAssociados}">
							<rich:column>
								<f:facet name="header"><h:outputText value="Pesquisador" /></f:facet>
								<h:outputText value="#{membro.pessoa.nome}"/>
							</rich:column>
					</rich:dataTable>
				</td>
			</tr>
		</a4j:region>
		</table>
		
		<br />
		<br />
		
		<a4j:region rendered="#{ propostaGrupoPesquisaMBean.exibirForm }">
			<table class="formulario" width="50%">
				<caption> Informa��es Pessoais </caption>

				<tr>
					<td style="width: 10%;"> <b>Nome: </b> </td>
					<td> 
						<h:outputText value="#{ propostaGrupoPesquisaMBean.membroPainel.pessoa.nome }" /> 
					</td>
				</tr>

				<tr>
					<td style="width: 10%;"> <b>Categoria: </b> </td>
					<td> 
						<h:outputText value="#{ propostaGrupoPesquisaMBean.membroPainel.categoriaString }" /> 
					</td>
				</tr>

				<tr>
					<td style="width: 10%;"> <b>Classifica��o: </b> </td>
					<td> 
						<h:outputText value="#{ propostaGrupoPesquisaMBean.membroPainel.classificacaoString }" /> 
					</td>
				</tr>

				<tr>
					<td style="width: 10%;"> <b>Tipo: </b> </td>
					<td> 
						<h:outputText value="#{ propostaGrupoPesquisaMBean.membroPainel.tipoMembroGrupoPesqString }" /> 
					</td>
				</tr>

				<tr>
					<td>
						<h:outputText id="labelCpf" rendered="#{!propostaGrupoPesquisaMBean.membroPainel.pessoa.internacional}">CPF:</h:outputText>
			   			<h:outputText id="labelPassaporte" rendered="#{propostaGrupoPesquisaMBean.membroPainel.pessoa.internacional}" >Passaporte:</h:outputText> 
					</td>
					<td> 
						<h:inputText value="#{ propostaGrupoPesquisaMBean.membroPainel.pessoa.cpf_cnpj }" maxlength="14" size="11" id="inputCpf" 
							onblur="formataCPF(this, event, null)" onkeypress="return formataCPF(this, event, null)" rendered="#{ !propostaGrupoPesquisaMBean.membroPainel.pessoa.internacional }">
								<f:converter converterId="convertCpf"/>
						</h:inputText>
						<h:inputText value="#{propostaGrupoPesquisaMBean.docenteExterno.pessoa.passaporte}" 
							size="22" maxlength="20" id="inputPassaporte" rendered="#{ propostaGrupoPesquisaMBean.membroPainel.pessoa.internacional }" />
					</td>
				</tr>
		
				<tr>
					<td> Senha: </td>
					<td> <h:inputSecret value="#{ propostaGrupoPesquisaMBean.membroPainel.senhaConfirmacao }" maxlength="7" size="9" /> </td>
				</tr>
		
				<tr>
					<td> Assinar? </td>
					<td> 
						<h:selectBooleanCheckbox value="#{ propostaGrupoPesquisaMBean.membroPainel.selecionado }" />
					</td>
				</tr>		
		
				<tfoot>
					<tr>
						<td colspan="2" style="text-align: center;">
							<h:commandButton action="#{ propostaGrupoPesquisaMBean.assinar  }" value="Assinar" /> 
							<h:commandButton action="#{ propostaGrupoPesquisaMBean.cancelar  }" value="Cancelar" />
						</td>
					</tr>
				</tfoot>
		  	</table>
		</a4j:region>
		
		<a4j:region rendered="#{ not propostaGrupoPesquisaMBean.exibirForm && propostaGrupoPesquisaMBean.exibeBotoes }">
		
			<h:panelGroup layout="block" styleClass="descricaoOperacao" rendered="#{not empty propostaGrupoPesquisaMBean.membroPainel.assinado && propostaGrupoPesquisaMBean.membroPainel.assinado}">
				<h3> <center> Voc� j� assinou digitalmente sua participa��o neste grupo de pesquisa. </center> </h3>
			</h:panelGroup>
			
			<h:panelGroup layout="block" styleClass="descricaoOperacao" rendered="#{empty propostaGrupoPesquisaMBean.membroPainel}">
				<h3> <center> O L�der do grupo removeu sua participa��o neste grupo de pesquisa. </center> </h3>
			</h:panelGroup>

			<h:panelGroup layout="block" styleClass="descricaoOperacao" rendered="#{ not propostaGrupoPesquisaMBean.membroPainel.assinado }">
				<h3> <center> Voc� j� assinou digitalmente sua n�o participa��o neste grupo de pesquisa. </center> </h3>
			</h:panelGroup>
		
		</a4j:region>
		
  </h:form>	

</f:view>

<%@include file="/public/include/rodape.jsp" %>
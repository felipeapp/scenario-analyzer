<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.list th { font-weight: bold; width: 25%; }
</style>

<f:view>

<h2><ufrn:subSistema></ufrn:subSistema> &gt; Grupo de Pesquisa</h2>
<h:form id="form">
<table class="formulario" width="90%">
	<caption> Dados do Grupo de Pesquisa </caption>
       <caption class="listagem">Dados da Proposta</caption>
    
        <tbody class="list">
	        <tr>
	        	<td colspan="2" class="subFormulario">1. Caracterização do Grupo</td>
	        </tr>
	        <tr>
	            <th>Título do Grupo:</th>
	            <td>
	                <h:outputText id="nome" value="#{propostaGrupoPesquisaMBean.obj.nome}" />
	            </td>
	        </tr>
			<tr>
				<th>Líder:</th>
				<td>
					<h:outputText id="coordenador" value="#{propostaGrupoPesquisaMBean.obj.coordenador.nome}" />
				</td>
			</tr>
			<tr>
				<th>Vice-Líder:</th>
				<td>
	 				<h:outputText id="viceCoordenador" value="#{propostaGrupoPesquisaMBean.obj.viceCoordenador.pessoa.nome}" />
				</td>
			</tr>
	        <tr>
	            <th>Área de Conhecimento:</th>
	            <td>
	            	<h:outputText id="areaConhecimento" value="#{ propostaGrupoPesquisaMBean.area.nome }" />
	            </td>
	        </tr>
	        <tr>
	        	<th>Sub-área de Conhecimento:</th>
	        	<td>
	        	    <h:outputText id="subareaCNPQ" value="#{propostaGrupoPesquisaMBean.obj.areaConhecimentoCnpq.id}" />
	        	</td>
	        </tr>
			<tr>
				<th> <b>Data da Última Atualização:</b> </th>
				<td> <ufrn:format type="data" valor="${ propostaGrupoPesquisaMBean.obj.dataUltimaAtualizacao }"/> </td>
			</tr>
	
			<a4j:region rendered="#{ not empty propostaGrupoPesquisaMBean.obj.membrosPermanentes }">
				<tr>
					<td colspan="2" class="subFormulario" style="padding-top: 10px;">2. Pesquisadores Permanentes</td>
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
							<rich:column>
								<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
								<h:outputText value="#{ membro.categoriaString }"/>
							</rich:column>
							<rich:column>
								<f:facet name="header"><f:verbatim>Classificação</f:verbatim></f:facet>
									<h:outputText value="<font color='red'>" rendered="#{membro.classificacao == 1}"  escape="false"/>
										<h:outputText value="#{membro.classificacaoString}" />
									<h:outputText value="</font>" rendered="#{membro.classificacao == 1}"  escape="false"/>
							</rich:column>
						</rich:dataTable>
					</td>
				</tr>
			</a4j:region>
		
			<a4j:region rendered="#{ not empty propostaGrupoPesquisaMBean.obj.membrosAssociados }">
				<tr>
					<td colspan="2" class="subFormulario" style="padding-top: 10px;">3. Pesquisadores Associados</td>
				</tr>
				<tr>
					<td colspan="2">
			
						<rich:dataTable id="dtAssociados" value="#{propostaGrupoPesquisaMBean.obj.membrosAssociados}" 
								binding="#{propostaGrupoPesquisaMBean.membrosAssociados}" 
								var="membro" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
							<rich:column>
								<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
								<h:outputText value="#{ membro.pessoa.nome }"/>
							</rich:column>
							<rich:column>
								<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
								<h:outputText value="#{ membro.categoriaString }"/>
							</rich:column>
							<rich:column>
								<f:facet name="header"><f:verbatim>Classificação</f:verbatim></f:facet>
								<h:outputText value="#{ membro.classificacaoString }"/>
							</rich:column>
						</rich:dataTable>
					</td>
				</tr>
			</a4j:region>
			<tr>
				<td colspan="2" class="subFormulario" style="padding-top: 10px;">4. Termo de Concordância</td>
			</tr>
			<tr>
				<td colspan="2">
			
						<rich:dataTable id="dtTermo" value="#{propostaGrupoPesquisaMBean.obj.equipesGrupoPesquisaCol}" 
								var="membro" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
					
							<rich:column>
								<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
								<h:outputText value="#{ membro.pessoa.nome }"/>
							</rich:column>
							<rich:column>
								<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
								<h:outputText value="#{ membro.categoriaString }"/>
							</rich:column>
							<rich:column>
								<f:facet name="header"><f:verbatim>Classificação</f:verbatim></f:facet>
									<h:outputText value="<font color='red'>" rendered="#{membro.classificacao == 1}"  escape="false"/>
										<h:outputText value="#{membro.classificacaoString}" />
									<h:outputText value="</font>" rendered="#{membro.classificacao == 1}"  escape="false"/>
							</rich:column>
							<rich:column width="30%">
								<f:facet name="header"><f:verbatim>Tipo</f:verbatim></f:facet>
								<h:outputText value="#{ membro.tipoMembroGrupoPesqString }"/>
							</rich:column>
							<rich:column width="5%">
								<f:facet name="header"><f:verbatim>Assinado</f:verbatim></f:facet>
								<h:graphicImage value="/img/check.png" style="overflow: visible;" height="16px;" rendered="#{ not empty membro.assinado && membro.assinado }"/>
								<h:graphicImage value="/img/delete_old.gif" style="overflow: visible;" height="16px;" rendered="#{ not empty membro.assinado && !membro.assinado }"/>
								<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" height="16px;" rendered="#{ empty membro.assinado }"/>
							</rich:column>
						</rich:dataTable>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario" style="padding-top: 10px;">5. Linhas de Pesquisa e Projetos Vínculados</td>
			</tr>
			<tr>
				<td colspan="2">
					<rich:dataTable id="gp" value="#{ propostaGrupoPesquisaMBean.obj.linhasPesquisaCol }" 
						var="linhas" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
		       			<rich:column styleClass="subFormulario">
								<f:facet name="header"><f:verbatim>Linha de Pesquisa / Projetos</f:verbatim></f:facet>
								<h:outputText value="#{ linhas.nome }"/>
		
								<rich:dataTable id="projetos" value="#{ linhas.projetosPesquisaCol }" 
									var="_proj" align="center" width="100%" styleClass="subFormulario" rowClasses="linhaPar">
				        			<rich:column>
										&nbsp;&nbsp;&nbsp; <h:outputText value="#{ _proj.titulo }"/>
				        			</rich:column>
		
				        		</rich:dataTable>
							
		       			</rich:column>
		       		</rich:dataTable>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario" style="padding-top: 10px;">Dados Gerais</td>
			</tr>
			<tr>
				<th  width="30%">Justificativa:</th>
				<td>
	 				<h:outputText id="just" value="#{ propostaGrupoPesquisaMBean.obj.justificativa }" />
				</td>
			</tr>
						
			<tr>
				<th>Instituições:</th>
				<td>
	 				<h:outputText id="inst" value="#{ propostaGrupoPesquisaMBean.obj.instituicoesIntercambio }" />
				</td>
			</tr>
						
			<tr>
				<th>Infraestrutura:</th>
				<td>
	 				<h:outputText id="infra" value="#{ propostaGrupoPesquisaMBean.obj.infraestrutura }" />
				</td>
			</tr>

			<tr>
				<th>Laboratório:</th>
				<td>
	 				<h:outputText id="lab" value="#{ propostaGrupoPesquisaMBean.obj.laboratorios }" />
				</td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario" style="padding-top: 10px;">Parecer</td>
			</tr>

	        <tr>
	            <th class="obrigatorio" style="font-weight: normal;">Status:</th>
	            <td>
	               <h:selectOneMenu id="status"
						value="#{propostaGrupoPesquisaMBean.obj.status}" style="width: 70%;" onchange="submit()">
						<f:selectItem itemValue="-1" itemLabel="-- SELECIONE UM STATUS --"/>
						<f:selectItems value="#{grupoPesquisa.tiposStatusCombo}"/>
					</h:selectOneMenu>
	            </td>
	        </tr>
	        
	        <a4j:region rendered="#{ propostaGrupoPesquisaMBean.necessitaCorrecao }">
		        <tr>
		            <th class="obrigatorio" style="font-weight: normal;">Parecer:</th>
		            <td>
		               <h:inputTextarea id="parecer" value="#{ propostaGrupoPesquisaMBean.obj.parecer }" cols="80" rows="10"/>
		            </td>
		        </tr>
	        </a4j:region>
	</tbody>
	
	<tfoot>
		<tr>
			<td colspan="3">
			
				<h:commandButton id="btnVoltar" value="<< Voltar" action="#{ grupoPesquisa.listar }" />
				<h:commandButton id="btnCadastrar" value="#{ propostaGrupoPesquisaMBean.confirmButton }" action="#{ propostaGrupoPesquisaMBean.submeterParecer }" />
			</td>
		</tr>	
	</tfoot>
 </table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
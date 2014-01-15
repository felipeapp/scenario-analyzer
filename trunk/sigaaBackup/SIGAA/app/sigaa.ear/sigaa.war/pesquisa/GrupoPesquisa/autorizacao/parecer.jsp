<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.list th { font-weight: bold; }
</style>

<f:view>

<h2><ufrn:subSistema></ufrn:subSistema> &gt;Grupo de Pesquisa</h2>
<h:form id="form">
<table class="formulario" width="90%">
	<caption> Dados do Grupo de Pesquisa </caption>
       <caption class="listagem">Dados da Proposta</caption>
    
        <tbody class="list">
	        <tr>
	        	<td colspan="2" class="subFormulario">1. Caracterização do Grupo</td>
	        </tr>
	        <tr>
	            <th width="30%">Título do Grupo:</th>
	            <td>
	                <h:outputText id="nome" value="#{autorizacaoGrupoPesquisaMBean.obj.nome}" />
	            </td>
	        </tr>
			<tr>
				<th>Coordenador:</th>
				<td>
					<h:outputText id="coordenador" value="#{autorizacaoGrupoPesquisaMBean.obj.coordenador.nome}" />
				</td>
			</tr>
			<tr>
				<th>Vice-Coordenador:</th>
				<td>
	 				<h:outputText id="viceCoordenador" value="#{autorizacaoGrupoPesquisaMBean.obj.viceCoordenador.pessoa.nome}" />
				</td>
			</tr>
	        <tr>
	            <th>Área de Conhecimento:</th>
	            <td>
	            	<h:outputText id="areaConhecimento" value="#{ autorizacaoGrupoPesquisaMBean.area.nome }" />
	            </td>
	        </tr>
	        <tr>
	        	<th>Sub-área de Conhecimento:</th>
	        	<td>
	        	    <h:outputText id="subareaCNPQ" value="#{autorizacaoGrupoPesquisaMBean.obj.areaConhecimentoCnpq.nome}" />
	        	</td>
	        </tr>
			<tr>
				<th> <b>Data da Última Atualização:</b> </th>
				<td> <ufrn:format type="data" valor="${ autorizacaoGrupoPesquisaMBean.obj.dataUltimaAtualizacao }"/> </td>
			</tr>
	
			<a4j:region rendered="#{ not empty autorizacaoGrupoPesquisaMBean.obj.membrosPermanentes }">
				<tr>
					<td colspan="2" class="subFormulario" style="padding-top: 10px;">2. Pesquisadores Permanentes</td>
				</tr>
		
				<tr>
					<td colspan="2">
						<rich:dataTable id="dtPermanentes" value="#{autorizacaoGrupoPesquisaMBean.obj.membrosPermanentes}" 
								var="membro" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
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
							<rich:column width="5%">
								<f:facet name="header"><f:verbatim>CV Lattes</f:verbatim></f:facet>
								
									<h:outputText escape="false" rendered="#{not empty membro.enderecoLattes}" value="
										<a href='#{membro.enderecoLattes}' target='_blank'>
										<img src='/sigaa/img/prodocente/lattes.gif' title='Currículo do Pesquisador na Plataforma Lattes'></img> 
									 </a>"/>
									<h:outputText escape="false" rendered="#{empty membro.enderecoLattes}" value="<img src='/sigaa/img/prodocente/question.png' title='Endereço do CV não registrado no sistema'></img> "/>
							</rich:column>
						</rich:dataTable>
					</td>
				</tr>
			</a4j:region>
		
			<a4j:region rendered="#{ not empty autorizacaoGrupoPesquisaMBean.obj.membrosAssociados }">
				<tr>
					<td colspan="2" class="subFormulario" style="padding-top: 10px;">3. Pesquisadores Associados</td>
				</tr>
				<tr>
					<td colspan="2">
			
						<rich:dataTable id="dtAssociados" value="#{autorizacaoGrupoPesquisaMBean.obj.membrosAssociados}" 
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
							<rich:column width="5%">
								<f:facet name="header"><f:verbatim>CV Lattes</f:verbatim></f:facet>
								
									<h:outputText escape="false" rendered="#{not empty membro.enderecoLattes}" value="
										<a href='#{membro.enderecoLattes}' target='_blank'>
										<img src='/sigaa/img/prodocente/lattes.gif' title='Currículo do Pesquisador na Plataforma Lattes'></img> 
									 </a>"/>
									<h:outputText escape="false" rendered="#{empty membro.enderecoLattes}" value="<img src='/sigaa/img/prodocente/question.png' title='Endereço do CV não registrado no sistema'></img> "/>
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
			
						<rich:dataTable id="dtTermo" value="#{autorizacaoGrupoPesquisaMBean.obj.equipesGrupoPesquisaCol}" 
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
				<td colspan="2" class="subFormulario" style="padding-top: 10px;">5. Linhas de Pesquisa e Projetos Vinculados</td>
			</tr>
			<tr>
				<td colspan="2">
					<rich:dataTable id="gp" value="#{ autorizacaoGrupoPesquisaMBean.obj.linhasPesquisaCol }" 
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
	 				<h:outputText id="just" value="#{ autorizacaoGrupoPesquisaMBean.obj.justificativa }" />
				</td>
			</tr>
						
			<tr>
				<th>Instituições:</th>
				<td>
	 				<h:outputText id="inst" value="#{ autorizacaoGrupoPesquisaMBean.obj.instituicoesIntercambio }" />
				</td>
			</tr>
						
			<tr>
				<th>Infraestrutura:</th>
				<td>
	 				<h:outputText id="infra" value="#{ autorizacaoGrupoPesquisaMBean.obj.infraestrutura }" />
				</td>
			</tr>

			<tr>
				<th>Laboratório:</th>
				<td>
	 				<h:outputText id="lab" value="#{ autorizacaoGrupoPesquisaMBean.obj.laboratorios }" />
				</td>
			</tr>

			<a4j:region rendered="#{ not empty autorizacaoGrupoPesquisaMBean.historicoGrupoPesquisa }">
				<tr>
					<td colspan="2" class="subFormulario" style="padding-top: 10px;">Histórico</td>
				</tr>
				<tr>
					<td colspan="2">
			
						<rich:dataTable id="dtHistorico" value="#{autorizacaoGrupoPesquisaMBean.historicoGrupoPesquisa }"							 
								var="h" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
							<rich:column>
								<f:facet name="header"><f:verbatim>Data/Hora</f:verbatim></f:facet>
								<h:outputText value="#{ h.data }">
									<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
								</h:outputText>
							</rich:column>
							<rich:column>
								<f:facet name="header"><f:verbatim>Status</f:verbatim></f:facet>
								<h:outputText value="#{ h.descricaoStatus }"/>
							</rich:column>
							<rich:column>
								<f:facet name="header"><f:verbatim>Usuário</f:verbatim></f:facet>
								<h:outputText value="#{ h.registroEntrada.usuario.nomeLogin }"/>
							</rich:column>
							<rich:column breakBefore="true" rendered="#{not empty h.parecer}" colspan="3">
								<f:verbatim><b>Parecer:</b> </f:verbatim>
								<h:outputText value="#{ h.parecer }"/>
							</rich:column>
						</rich:dataTable>
					</td>
				</tr>
			</a4j:region>

			<tr>
				<td colspan="2" class="subFormulario" style="padding-top: 10px;">Parecer</td>
			</tr>

	        <tr>
	            <th class="obrigatorio" style="font-weight: normal;">Status:</th>
	            <td>
	               <h:selectOneMenu id="status"
						value="#{autorizacaoGrupoPesquisaMBean.obj.status}" style="width: 70%;">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM STATUS --"/>
						<f:selectItems value="#{autorizacaoGrupoPesquisaMBean.tiposStatusParecerCombo}"/>
					</h:selectOneMenu>
	            </td>
	        </tr>
	        
	        <tr>
	            <th class="obrigatorio" style="font-weight: normal;">Parecer:</th>
	            <td>
	               <h:inputTextarea id="parecer" value="#{ autorizacaoGrupoPesquisaMBean.obj.parecer }" cols="80" rows="10"/>
	            </td>
	        </tr>

	</tbody>
	
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton id="btnSubmeterDadosGerais" value="Cadastrar Parecer" action="#{ autorizacaoGrupoPesquisaMBean.emitirParecer }" />
				<h:commandButton id="btnCancelar" value="Cancelar" onclick="#{confirm}" action="#{ autorizacaoGrupoPesquisaMBean.cancelar }" />
			</td>
		</tr>
	</tfoot>
 </table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
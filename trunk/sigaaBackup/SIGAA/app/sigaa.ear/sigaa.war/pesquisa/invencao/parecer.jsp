<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<style>
	table.visualizacao tr td.subFormulario {
		padding: 3px 0 3px 20px;
	}
	p.corpo {
		padding: 2px 8px 10px;
		line-height: 1.2em;
	}
</style>

<f:view>
<h2> <ufrn:subSistema /> &gt; Invenção &gt; Emissão de Parecer </h2>

<h:form id="form">
	
	<table class="formulario" width="100%">
		<caption class="listagem">Resumo da Invenção</caption>
		
		<tr>
			<td colspan="2" class="subFormulario"> 1. Título da Invenção </td>
		</tr>
	
		<tr>
			<th width="25%"><b>Título:</b></th>
			<td>
				<h:outputText id="titulo" value="#{parecerInvencaoBean.obj.invencao.titulo}" />
			</td>
		</tr>
		
		<tr>
			<th><b>Tipo:</b></th>
			<td>
				<h:outputText id="tipo" value="#{parecerInvencaoBean.obj.invencao.tipo.descricao}" />
			</td>
		</tr>
	
		<tr>
			<td colspan="2" class="subFormulario"> 2. Palavras-Chave </td>
		</tr>
	
		<tr>
			<th><b>Palavras-chave (Português):</b></th>
			<td>
				<h:outputText id="palavrasChavePortugues" value="#{parecerInvencaoBean.obj.invencao.palavrasChavePortugues}"/>
			</td>
		</tr>
	
		<tr>
			<th><b>Palavras-chave (Inglês):</b></th>
			<td>
				<h:outputText id="palavrasChaveIngles" value="#{parecerInvencaoBean.obj.invencao.palavrasChaveIngles}"/>
			</td>
		</tr>			
		
		<tr>
			<td colspan="2" class="subFormulario"> 3. Caracterização da Invenção </td>
		</tr>
		
		<tr>
			<th colspan="2" style="text-align: left;"><b>Descrição Resumida</b></th>
		</tr>
		<tr>
			<td colspan="2">
				<p class="corpo">
					<h:outputText id="descricao-resumida" value="#{parecerInvencaoBean.obj.invencao.descricaoResumida}" />
				</p>
			</td>
		</tr>
	
		<tr>
			<th colspan="2" style="text-align: left;"><b>Descrição Completa</b></th>
		</tr>
		<tr>
			<td colspan="2">
				<t:dataTable id="dataTableArquivos" value="#{parecerInvencaoBean.obj.invencao.arquivos}" var="arquivo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
					<t:column>
						<f:facet name="header"><f:verbatim>Descrição do arquivo</f:verbatim></f:facet>
						<h:outputText value="#{arquivo.descricao}" rendered="#{arquivo.categoria == 1}"/>
					</t:column>
					<t:column width="5%">
						<h:commandLink title="Visualizar Arquivo"  action="#{invencao.viewArquivo}" immediate="true" rendered="#{arquivo.categoria == 1}">
						        <f:param name="idArquivo" value="#{arquivo.idArquivo}"/>
					    		<h:graphicImage url="/img/view.gif" />
						</h:commandLink>
					</t:column>
				</t:dataTable>
			</td>
		</tr>
		
		<tr>
			<th colspan="2" style="text-align: left;"><b>Estado da Técnica</b></th>
		</tr>
		<tr>
			<td colspan="2">
				<p class="corpo">
					<h:outputText id="estado-tecnica" value="#{parecerInvencaoBean.obj.invencao.estadoTecnica}" />
				</p>
			</td>
		</tr>
		
		<tr>
			<th colspan="2" style="text-align: left;"><b>Registro/Documentação</b></th>
		</tr>
		<tr>
			<td colspan="2">
				<p class="corpo">
					<h:outputText id="registro-documentacao" value="#{parecerInvencaoBean.obj.invencao.registroDocumentacao}" />
				</p>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" class="subFormulario"> 4. Revelação da Invenção </td>
		</tr>
		<tr>
			<td colspan="2">
				<t:dataTable id="dataTableRevelacoes" value="#{parecerInvencaoBean.obj.invencao.arquivos}" var="arquivo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
					<t:column>
						<f:facet name="header"><f:verbatim>Publicação</f:verbatim></f:facet>
						<h:outputText value="#{arquivo.descricao}" rendered="#{arquivo.categoria == 2}"/>
					</t:column>
					<t:column>
						<f:facet name="header"><f:verbatim>Data</f:verbatim></f:facet>
						<h:outputText value="#{arquivo.data}" rendered="#{arquivo.categoria == 2}"/>
					</t:column>
					<t:column width="5%">
						<h:commandLink title="Visualizar Arquivo"  action="#{invencao.viewArquivo}" immediate="true" rendered="#{arquivo.categoria == 2}">
						        <f:param name="idArquivo" value="#{arquivo.idArquivo}"/>
					    		<h:graphicImage url="/img/view.gif" />
						</h:commandLink>
					</t:column>
				</t:dataTable>
			</td>
		</tr>
	
		<tr>
			<td colspan="2" class="subFormulario"> 5. Utilização de material biológico/genético </td>
		</tr>
		<tr>
			<td colspan="2">
				<p class="corpo">
					<h:outputText id="utilizacao-material" value="#{parecerInvencaoBean.obj.invencao.utilizacaoMaterial}" />
				</p>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" class="subFormulario"> 6. Utilização de software </td>
		</tr>
		<tr>
			<td colspan="2">
				<p class="corpo">
					<h:outputText id="utilizacao-software" value="#{parecerInvencaoBean.obj.invencao.utilizacaoSoftware}" />
				</p>
			</td>
		</tr>
		
		<tr>
			<td class="subFormulario" colspan="2"> 7. Vínculos Institucionais da Invenção a Programas Estratégicos da ${ configSistema['siglaInstituicao'] } </td>
		</tr>
		<tr>
			<td colspan="2">
				<t:dataTable id="dataTableVinculos" value="#{parecerInvencaoBean.obj.invencao.vinculos}" var="vinculo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
					<t:column>
						<f:facet name="header"><f:verbatim>Projeto/Grupo de Pesquisa</f:verbatim></f:facet>
						<h:outputText value="#{vinculo.descricao}" />
					</t:column>
				</t:dataTable>
			</td>
		</tr>
		
		<tr>
			<td class="subFormulario" colspan="2"> 8. Financiamentos da Invenção </td>
		</tr>
		<tr>
			<td colspan="2">
				<t:dataTable id="dataTableFinanciamentos" value="#{parecerInvencaoBean.obj.invencao.financiamentos}" var="financiamento" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
					<t:column>
						<f:facet name="header"><f:verbatim>Entidade Financiadora</f:verbatim></f:facet>
						<h:outputText value="#{financiamento.entidadeFinanciadora.nome}" />
					</t:column>
					<t:column >
						<f:facet name="header"><f:verbatim>Título</f:verbatim></f:facet>
						<h:outputText value="#{financiamento.tituloProjeto}" />
					</t:column>
					<t:column>
						<f:facet name="header"><f:verbatim>Código</f:verbatim></f:facet>
						<h:outputText value="#{financiamento.codigoProjeto}" />
					</t:column>
					<t:column>
						<f:facet name="header"><f:verbatim>Valor do Financiamento</f:verbatim></f:facet>
						<h:outputText value="#{financiamento.valor}">
							<f:convertNumber pattern="R$ #,##0.00"/>
						</h:outputText>
					</t:column>
				</t:dataTable>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" class="subFormulario"> 9. Próximas etapas </td>
		</tr>
		<tr>
			<td colspan="2">
				<p class="corpo">
					<h:outputText id="prox-etapas" value="#{parecerInvencaoBean.obj.invencao.proximasEtapas}" />
				</p>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" class="subFormulario"> 10. Mercados e capacidade tecnológica </td>
		</tr>
		<tr>
			<td colspan="2">
				<p class="corpo">
					<h:outputText id="paises" value="#{parecerInvencaoBean.obj.invencao.mercadosCapacidadeTecnologica}" />
				</p>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" class="subFormulario"> 11. Empresas </td>
		</tr>
		<tr>
			<td colspan="2">
				<p class="corpo">
					<h:outputText id="empresas" value="#{parecerInvencaoBean.obj.invencao.empresas}" />
				</p>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" class="subFormulario"> 12. Autores da Invenção </td>
		</tr>
		<tr>
			<td colspan="2">
				<t:dataTable id="dataTableInventores" value="#{parecerInvencaoBean.obj.invencao.inventores}" var="inventor" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
	
					<t:column>
						<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
						<h:outputText value="#{inventor.pessoa.nome}" />
					</t:column>
					
					<t:column>
						<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
						<h:outputText value="#{inventor.categoriaMembro.descricao}" rendered="#{not empty inventor.categoriaMembro}" />
					</t:column>
					
					<t:column>
						<f:facet name="header"><f:verbatim>Departamento / Curso / Instituição</f:verbatim></f:facet>
						<h:outputText value="#{inventor.servidor.unidade.sigla}" rendered="#{not empty inventor.servidor}" />
						<h:outputText value="#{inventor.docente.unidade.sigla}" rendered="#{not empty inventor.docente}" />
						<h:outputText value="#{inventor.discente.curso.descricao}" rendered="#{not empty inventor.discente}" />
						<h:outputText value="#{inventor.docenteExterno.instituicao.nome}" rendered="#{not empty inventor.docenteExterno}" />
					</t:column>
	
				</t:dataTable>
			</td>
		</tr>
		
		<tr>
			<td colspan="2"> &nbsp; </td>
		</tr>
		
		<tr>
			<td colspan="2" class="subFormulario"> Pareceres Emitidos </td>
		</tr>
		
		<tr>
			<td colspan="2">
				<t:dataTable id="dataTablePareceres" value="#{parecerInvencaoBean.pareceresAnteriores}" var="par" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
	
					<t:column>
						<f:facet name="header"><f:verbatim>Posição</f:verbatim></f:facet>
						<h:outputText value="#{par.favoravel ? 'Favorável': 'Desfavorável'}" />
					</t:column>
					
					<t:column>
						<f:facet name="header"><f:verbatim>Texto</f:verbatim></f:facet>
						<h:outputText value="#{par.texto}" />
					</t:column>
					
					<t:column>
						<f:facet name="header"><f:verbatim>Usuário</f:verbatim></f:facet>
						<h:outputText value="#{par.registroEntrada.usuario.login}" />
					</t:column>
					
					<t:column>
						<f:facet name="header"><f:verbatim>Data</f:verbatim></f:facet>
						<h:outputText value="#{par.data}">
							<f:convertDateTime pattern="dd/MM/yyyy"/>
						</h:outputText>
					</t:column>
	
				</t:dataTable>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" class="subFormulario"> Dados do Parecer </td>
		</tr>
		
		<h:inputHidden id="invencao" value="#{parecerInvencaoBean.obj.invencao.id}"/>
		
		<tr>
			<td colspan="2"><b>Posição:</b></td>
		</tr>
		<tr>
			<td colspan="2">
				 <h:selectOneRadio id="favoravel" value="#{parecerInvencaoBean.obj.favoravel}">
				 	<f:selectItem itemLabel="Favorável" itemValue="true"/>
				 	<f:selectItem itemLabel="Desfavorável" itemValue="false"/>
				 </h:selectOneRadio>
			</td>
		</tr>
		<tr>
			<td colspan="2"><b>Parecer:</b></td>
		</tr>
		<tr>
			<td colspan="2">
				 <h:inputTextarea id="texto" value="#{parecerInvencaoBean.obj.texto}" cols="2" rows="5" style="width: 95%"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				 <h:selectBooleanCheckbox id="notificacao" value="#{parecerInvencaoBean.obj.notificarEmail}" />
				 Notificar interessado por email
			</td>
		</tr>
		<ufrn:checkRole papel="<%= SigaaPapeis.GESTOR_PESQUISA %>">
			<tr>
				<td width="15%"> Status Atual: </td>
				<td> <h:outputText value="#{parecerInvencaoBean.obj.invencao.statusString}"/> </td>
			</tr>
			<tr>
				<td width="15%"> Novo Status: </td>
				<td>
					<h:selectOneMenu id="status" value="#{parecerInvencaoBean.obj.status}">
						<f:selectItems value="#{parecerInvencaoBean.statusInvencaoCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
		</ufrn:checkRole>
		<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Emitir Parecer" action="#{parecerInvencaoBean.emitirParecer}" id="btConfirmar" /> 
				<h:commandButton value="Cancelar" action="#{parecerInvencaoBean.cancelar}" onclick="#{confirm}" immediate="true" id="btCancelar"/>
			</td>
		</tr>
		</tfoot>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

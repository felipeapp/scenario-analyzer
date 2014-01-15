<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:region rendered="#{ propostaGrupoPesquisaMBean.portalDocente }">
<%@include file="/portais/docente/menu_docente.jsp"%>
</a4j:region>
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

<h2><ufrn:subSistema /> &gt; Termo de Concordância</h2>

	<div class="descricaoOperacao">
		<p>
			"Os Membros relacionados abaixo, declaram, para fins de cumprimento do Inciso IV, do Art. 7º do Anexo da resolução nº 162/2008_CONSEPE, 
			de 18 de Novembro de 2008, estarem de acordo com a associação de seus nomes ao Grupo de Pesquisa <b> <h:outputText value="#{propostaGrupoPesquisaMBean.obj.nome}" /> </b>,
		    líderado pelo Professor(a): <b> <h:outputText value="#{ propostaGrupoPesquisaMBean.obj.coordenador.nome }" /> </b>." 
		</p>
	</div>
	
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/certificate.png" style="overflow: visible;" width="19px;" />: Emitir Certificado
		<h:graphicImage value="/img/certificate_off.png" style="overflow: visible;"  width="19px;" />: Certificado não Disponível
	</div>

	<h:form id="form">
		<table class="formulario" width="100%">
			<caption> Membros do Grupo de Pesquisa </caption>
			<tbody>
				<tr>
					<td colspan="2">
						<rich:dataTable id="dtAssociados" value="#{propostaGrupoPesquisaMBean.obj.equipesGrupoPesquisaCol}" 
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
							<rich:column width="30%">
								<f:facet name="header"><f:verbatim>Tipo</f:verbatim></f:facet>
								<h:outputText value="#{ membro.tipoMembroGrupoPesqString }"/>
							</rich:column>
							<rich:column width="5%">
								<f:facet name="header"><f:verbatim>Declaração</f:verbatim></f:facet>
								<h:commandLink action="#{declaracaoGrupoPesquisaMBean.selecionarMembroGrupoPesquisa}" rendered="#{ not empty membro.assinado && membro.assinado }"
									title="Emitir Certificado">
										<h:graphicImage value="/img/certificate.png" style="overflow: visible;" height="19px;" />
										<f:param name="id" value="#{ membro.id }"/>
								</h:commandLink>
								<h:graphicImage value="/img/certificate_off.png" style="overflow: visible;" height="19px;" rendered="#{ empty membro.assinado || (not empty membro.assinado && !membro.assinado) }"
									title="Certificado não Disponível"/>
							</rich:column>
						</rich:dataTable>
					</td>
				</tr>
			</tbody>
			
				<tfoot>
					<tr>
						<td colspan="4">
							<h:commandButton id="btnListar" value="<< Voltar" action="#{ grupoPesquisa.lista }" />
						</td>
					</tr>
				</tfoot>
		</table>
	</h:form>
	
</f:view>

<script type="text/javascript">
	// função que seleciona ou deseleciona todos os checkbox da página de acordo com o valor do checkbox passado como referencia
	function selecionarTudo(chk){
	   for (i=0; i<document.form.elements.length; i++)
	      if(document.form.elements[i].type == "checkbox")
	         document.form.elements[i].checked= chk.checked;
	}
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
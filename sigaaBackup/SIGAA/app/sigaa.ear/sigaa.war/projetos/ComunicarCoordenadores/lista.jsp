<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%> 

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Comunicar Coordenadores de A��es Acad�micas Integradas</h2>
	<div class="descricaoOperacao">
					<b>Caro usu�rio:</b>
					<br /> * Busque as a��es com os filtros desejados.
					<br /> * Selecione as a��es nas quais deseja enviar um comunicado para o coordenador correspondente e v� para o pr�ximo passo.
					<br /> * No pr�ximo passo preencha o comunicado e envie.
			</div>
	
	<%@include file="/projetos/form_busca_projetos.jsp"%>
	<c:set var="acoes" value="#{buscaAcaoAssociada.resultadosBusca}"/>
	
	<c:if test="${not empty acoes}">
		<h:form id="frm">
			
			<table class="listagem">
				<caption>A��es Acad�micas localizadas (${ fn:length(acoes) })</caption>
				<tr>
					<td>
						<h:panelGroup id="listaCheckbox">
							<rich:dataTable var="acao" value="#{buscaAcaoAssociada.resultadosBusca}" 
								rowClasses="linhapar, linhaImpar" style="width: 100%; border: 0;" >
								
								<rich:column>
									<f:facet name="header">
										<f:verbatim>
											<a href="javascript:selectAllCheckBox();"
												style="fontColor: blue">Todos</a>
										</f:verbatim>
									</f:facet>
									<center><h:selectBooleanCheckbox id="checkSelecionada" value="#{acao.selecionado}"/></center>
								</rich:column>
								
								<rich:column>
									<f:facet name="header">
										<f:verbatim>Ano</f:verbatim>
									</f:facet>
									<h:outputText value="#{acao.ano}" />
								</rich:column>
								
								<rich:column>
									<f:facet name="header">
										<f:verbatim>T�tulo</f:verbatim>
									</f:facet>
									<h:outputText value="#{acao.titulo}" />
									<h:outputText value="<br/><i>Coordenador(a): #{acao.coordenador.pessoa.nome}</i>" rendered="#{not empty acao.coordenador}" escape="false"/>
								</rich:column>
								
								<rich:column>
									<f:facet name="header">
										<f:verbatim>Unidade</f:verbatim>
									</f:facet>
									<h:outputText value="#{acao.unidade.sigla}" />
								</rich:column>
								
								<rich:column>
									<f:facet name="header">
										<f:verbatim>Situa��o</f:verbatim>
									</f:facet>
									<h:outputText value="#{acao.situacaoProjeto.descricao}" />
								</rich:column>
								
							</rich:dataTable>
						
						</h:panelGroup>
					</td>
				</tr>
				
				<tfoot>
					<tr>
						<td align="center" colspan="5">
							<h:commandButton value="Pr�ximo passo >>" action="#{comunicarCoordenadores.comunicarCoordenadores}" />
						</td>
					</tr>
				</tfoot>
			</table>
			
		</h:form>
	</c:if>
</f:view>
<script type="text/javascript">

	var checkflag = "false";

	function selectAllCheckBox() {
	    var div = document.getElementById('frm:listaCheckbox');
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
	window.onload(selectAllCheckBox());
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
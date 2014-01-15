<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.TipoProjetoEnsino"%>
<style>
	#moldura-interna{
		margin:0px auto;	
	    width:925px;background-color:#D3DDF1;
	}
	#moldura-interna div#top{
	    width:925px;background-color:#D3DDF1;
	}
	#moldura-interna div#top span{
	    display:block;width:925px;background-color:#D3DDF1;
	}
</style>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h:messages showDetail="true" showSummary="true" />
	<h2><ufrn:subSistema /> > Cadastro de Projeto de Ensino</h2>
	
	<div class="descricaoOperacao">
		<table width="100%">
			<tr>
				<td>Nesta tela devem ser informadas a dimensão da proposta.</td>
				<td><%@include file="passos_projeto.jsp"%></td>
			</tr>
		</table>
	</div>
	
	
	<h:form enctype="multipart/form-data"  id="form">
	<h:outputText  value="#{projetoMonitoria.create}"/>
	<h:outputText  value="#{unidade.create}"/>
	<h:inputHidden value="#{projetoMonitoria.confirmButton}"/>
	<h:inputHidden value="#{projetoMonitoria.obj.id}"/>
	
	<c:set var="PROJETO_MONITORIA" 		value="<%= String.valueOf(TipoProjetoEnsino.PROJETO_DE_MONITORIA) %>" 	scope="application"/>
	<c:set var="PROJETO_PAMQEG" 		value="<%= String.valueOf(TipoProjetoEnsino.PROJETO_PAMQEG) %>" 		scope="application"/>	

		<table class=formulario width="100%">
		<caption>Dimensão da proposta</caption>
		<tr>
			<td>
				<br clear="all"/>
				<div id="moldura-interna">
				 	<div id="top">
						<span>
							<table>						
							    <tr>
							      <td align="justify">
							          <b><font size="2px">Caro Docente,</b><br/>
							          <i>o preenchimento do formulário de apresentação das propostas de projeto, deve ser feito de forma que a redação permita 
							          entender com clareza as diferentes possibilidades de associação entre as dimensões de projeto de monitoria, bem como, 
							          a dimensão de projeto de melhoria da qualidade do ensino de graduação, visando possibilitar a aderência aos critérios de 
							          seleção dispostos no edital.
							          </i>
							          </font>
							      </td>
							    </tr>  
							</table>    
						</span>
				    </div>
			  </div>
			<br/>
	        <div id="moldura-interna" >
	                <div id="top">
	                <span>
							<table align="center">
								<tr>
									<td>
										<h:selectBooleanCheckbox value="#{ projetoMonitoria.checkMonitoria}" styleClass="noborder"/> PROJETO DE MONITORIA
									</td>
									<td>
										<h:selectBooleanCheckbox value="#{ projetoMonitoria.checkPAMQEG}" styleClass="noborder"/> PROJETO DE APOIO À MELHORIA DA QUALIDADE DO ENSINO DE GRADUAÇÃO - PAMQEG
									</td>
								</tr>
							</table>
					</span>
				</div>
			</div>
			<br clear="all"/>			
		</td>
		</tr>
	
		<tfoot>
			<tr> 
				<td colspan=3>			
					<h:commandButton value="Avançar >>" action="#{ projetoMonitoria.submeterDimensao }" id="btSubmeterDimensao" />
					<h:commandButton value="Cancelar" action="#{projetoMonitoria.cancelar}" onclick="#{confirm}" id="btCancelar" immediate="true" />
				</td> 
			</tr>
		</tfoot>
	</table>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>			
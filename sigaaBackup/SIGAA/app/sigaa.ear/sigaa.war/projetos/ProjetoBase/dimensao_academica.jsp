<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<style>
	#moldura-interna{
		margin:0px auto;	
	    width:725px;background: transparent 
	url('../../img/projeto/moldura_bg.png') repeat;
	}
	#moldura-interna div#top{
	    width:725px;background: transparent 
	url('../../img/projeto/moldura_top.png') top no-repeat;
	}
	#moldura-interna div#top span{
	    display:block;width:725px; background: transparent 
	url('../../img/projeto/moldura_bottom.png') bottom no-repeat;
	}
</style>
<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>

<h:messages showDetail="true"></h:messages>
<h2><ufrn:subSistema /> > Submiss�o de proposta de a��es acad�micas integradas </h2>

<div class="descricaoOperacao">
	<table width="100%">
		<tr>
			<td width="50%">
			Nesta tela deve ser informada a dimens�o acad�mica da proposta.
			</td>
			<td>
				<%@include file="passos_projeto.jsp"%>
			</td>
		</tr>
	</table>
</div>

<h:form id="form">
	<h:inputHidden value="#{projetoBase.confirmButton}"/>
	
			<center>
	<table class=formulario width="100%">
		<caption>Dimens�o acad�mica da proposta</caption>
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
							          <i>o preenchimento do formul�rio de apresenta��o das propostas de projeto deve ser feito de forma 
							          que a reda��o permita entender com clareza as diferentes possibilidades de associa��o entre as dimens�es 
							          acad�micas de ensino, pesquisa e extens�o, bem como as no��es de interdisciplinaridade e multiprofissionalidade, 
							          visando possibilitar a ader�ncia aos crit�rios de sele��o dispostos no edital.
							          </i>
							          </font>
							      </td>
							    </tr>  
							</table>    
						</span>
				    </div>
			  </div>
			<br/>
	        <div id="moldura-interna">
	                <div id="top">
	                <span>
							<table align="center">
								<tr>
									<td width="15px" >
										<img src="../../img/icones/monitoria.gif" />
									</td>
									<td width="120px" style="font-weight: bold"><h:selectBooleanCheckbox value="#{projetoBase.obj.ensino}"/> Ensino</td>
									
									<td width="15px">
			                            <img src="../../img/icones/potion_red.png"/>
			                        </td>		
			                        <td width="120px" style="font-weight: bold"> <h:selectBooleanCheckbox value="#{projetoBase.obj.pesquisa}"/> Pesquisa</td>
									
									<td width="15px">
			                            <img src="../../img/icones/houses.png"/>
			                        </td>
			                        <td width="120px" style="font-weight: bold"> <h:selectBooleanCheckbox value="#{projetoBase.obj.extensao}"/> Extens�o</td>
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
					<h:commandButton value="Cancelar" action="#{projetoBase.cancelar}"   onclick="#{confirm}" id="btCancelar"/>
					<h:commandButton value="Avan�ar >>" action="#{projetoBase.submeterDimensaoAcademica}" id="btSubmeterDimensaoAcademica"/>
				</td> 
			</tr>
		</tfoot>
	</table>
	</center>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>

<h:messages showDetail="true"></h:messages>
<h2><ufrn:subSistema /> > Anexar Fotos</h2>

	<div class="descricaoOperacao">
		<table width="100%">
			<tr>
				<td>
					<html:img page="/img/warning.gif"/>
					<font color="red" size="2">Atenção:</font>
					Utilize este espaço para enviar foto ou qualquer outra imagem que julgar importante para aprovação e/ou execução
					do projeto de ensino que está sendo cadastrada.			
					<br/>
					<br/>
				</td>
				<td>
					<%@include file="passos_projeto.jsp"%>
				</td>
			</tr>
		</table>
	</div>



<h:form id="formAnexosAtividade" enctype="multipart/form-data">
<table class="formulario" width="100%">

	<caption class="listagem">Informe os dados do arquivo de foto </caption>

	<h:inputHidden value="#{atividadeExtensao.confirmButton}"/>
	<h:inputHidden value="#{projetoMonitoria.obj.id}" id="idAtividade"/>


	<tr>
		<th width="23%"><b>Ano - Título:</b></th>
		<td>
			<h:outputText id="titulo" value="#{projetoMonitoria.obj.anoTitulo}">
				<f:attribute name="lineWrap" value="120"/>
				<f:converter converterId="convertTexto"/>
			</h:outputText>
		</td>
	</tr>

	<tr>
		<th  class="required"> Descrição:</th>
		<td>
			<h:inputText  id="descricao" value="#{projetoMonitoria.descricaoFoto}" size="83" maxlength="90"/>
		</td>
	</tr>

	<tr>
		<th  class="required">Arquivo de Foto:</th>
		<td>
			<t:inputFileUpload id="uFile" value="#{projetoMonitoria.foto}" storage="file" size="60"/>
		</td>
	</tr>
	
	<tr>
		<td colspan="2" align="center" style="background-color: #EBEBEB;	text-align: center;">
			
			<h:commandButton	action="#{projetoMonitoria.anexarFoto}" value="Anexar Foto" id="btAnexarFoto" style="background-color: #EBEBEB;	text-align: center;"/>
			<br/>
		</td>
	</tr>
	
	
	<c:if test="${empty projetoMonitoria.fotosProjeto}">
		<tr>
			<td width="100%" align="center" colspan="2"> <br /><i> Nenhuma foto anexada </i> <br /><br /></td>
		</tr>
	</c:if>
	
	
	<c:if test="${not empty projetoMonitoria.fotosProjeto}">
	<tr>
		<td colspan="2">
			<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Foto	    		
	    		<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Foto	    		
			</div>
		</td>		
	</tr>

	<tr>
		<td colspan="2" class="subFormulario">	Lista de fotos do projeto de ensino </td>
	</tr>

	<tr>
		<td colspan="2">
			<input type="hidden" value="0" id="idFotoOriginal" name="idFotoOriginal"/>
			<input type="hidden" value="0" id="idFotoMini" name="idFotoMini"/>
			<input type="hidden" value="0" id="idFotoProjeto" name="idFotoProjeto"/>

			<t:dataTable id="dataTableFotos" value="#{projetoMonitoria.fotosProjeto}" var="foto" align="center" 
				width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">

				<t:column>
					<f:facet name="header"><f:verbatim>Foto</f:verbatim></f:facet>
					<f:verbatim >
						<div class="foto">							
							<h:graphicImage url="/verFoto?idFoto=#{foto.idFotoMini}&key=#{sf:generateArquivoKey(foto.idFotoMini)}" width="70" height="70"/>
						</div>
					</f:verbatim>
				</t:column>

				<t:column>
					<f:facet name="header"><f:verbatim>Descrição da Foto</f:verbatim></f:facet>
					<h:outputText value="#{foto.descricao}" />
				</t:column>


				<t:column width="5%">
						<h:outputLink value="/sigaa/verFoto?idFoto=#{foto.idFotoOriginal}&key=#{sf:generateArquivoKey(foto.idFotoOriginal)}" id="link_verfoto_original_" title="Visualizar Foto">
							<h:graphicImage url="/img/view.gif" />
						</h:outputLink>						
					<h:commandButton image="/img/delete.gif" action="#{projetoMonitoria.removeFoto}"
						title="Remover Foto"  alt="Remover Foto"   onclick="$(idFotoMini).value=#{foto.idFotoMini};$(idFotoOriginal).value=#{foto.idFotoOriginal};$(idFotoProjeto).value=#{foto.id};return confirm('Deseja Remover esta Foto do projeto?')" id="remFoto" />
				</t:column>
				
			</t:dataTable>
		</td>
	</tr>
	</c:if>

	<tfoot>
	<tr> <td colspan="2">		
		<h:commandButton value="Gravar Proposta" action="#{ projetoMonitoria.cadastrarParcialDadosGerais }" title="Gravar Proposta para Continuar Depois." id="btGravar" />
		<h:commandButton value="<< Voltar" action="#{projetoMonitoria.passoAnterior}" id="btDadosProjeto" />
		<h:commandButton value="Cancelar" action="#{projetoMonitoria.cancelar}" onclick="#{confirm}" id="btCancelar" />		
		<h:commandButton value="Avançar >>" action="#{projetoMonitoria.submeterFotos}" id="btSubmeterFotos" />
	</td> </tr>
	</tfoot>

</h:form>
</table>
<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
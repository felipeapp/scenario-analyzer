<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<style>
	span.info {
		font-size: 0.95em;
		color: #555;
		line-height: 1.3em;
	}
	.btnExcluirFoto{
		background-color: #EFEFEF;
		padding: 2px 13px;
		font-weight: normal;
		font-variant: normal;
	}
	.fotoPerfil{
		background-color: #EFEFEF;
		border:#EFEFEF 8px solid;
	}
</style>

	<h2>Editar Dados do Site Pessoal do Docente</h2>
	<h:outputText value="#{perfilDocente.create}" />
	<h:form enctype="multipart/form-data">
		<table class="formulario" style="width: 90%;">
			<caption> Meu perfil </caption>
			<tbody>
			<tr>
				<td align="center" rowspan="5">
					<c:if test="${usuario.idFoto != null}">
						<img class="fotoPerfil" src="/sigaa/verFoto?idFoto=${usuario.idFoto}&key=${ sf:generateArquivoKey(usuario.idFoto) }" width="100" height="125" />
						<br/>
						<h:commandLink id="btnExcluirFoto" styleClass="btnExcluirFoto"  value="X Excluir Foto"
						 action="#{perfilDocente.cadastrar}" onclick="#{confirmDelete}"  >
							<f:param id="idFoto" name="idFoto" value="#{usuario.idFoto}" />
						</h:commandLink>
					</c:if>
					<c:if test="${usuario.idFoto == null}">
						<img class="fotoPerfil" src="/sigaa/img/no_picture.png" width="100" height="125" />
					</c:if>
				</td>
			</tr>
			<tr>
				<td>
					<b>Alterar Foto:</b> <t:inputFileUpload size="48" value="#{perfilDocente.foto}" />
				</td>
			</tr>		
			<tr>
				<td>
					<b>Endere�o Profissional:</b> 
					<h:inputTextarea value="#{perfilDocente.obj.endereco}" style="width: 97%;" rows="3" id="textEnderecoProfissional" label="Endere�o Profissional" 
					validatorMessage="Endere�o Profissional: M�ximo de 300 caracteres.">
						 <f:validateLength maximum="300"/>						
					</h:inputTextarea>
				</td>
			</tr>
			<tr>
				<td>
					<b><h:outputText value="Ocultar E-mail na �rea P�blica?"/></b>
					<h:selectOneMenu value="#{perfilDocente.obj.ocultarEmail}" id="ocultarEmail">
						<f:selectItems value="#{perfilDocente.simNao}"/>
					</h:selectOneMenu>
				</td>
			</tr>				
			<tr>
				<td>
					<b>N�mero da Sala:</b> <h:inputText value="#{perfilDocente.obj.sala}" size="10" maxlength="10" />
				</td>
			</tr>
			<tr>
				<td colspan="3">
					<b>Endere�o para acessar CV na Plataforma Lattes:</b> 
					<span class="info">(Utilize o endere�o dispon�vel em sua p�gina no Lattes)</span> <br />
					<h:inputText value="#{perfilDocente.obj.enderecoLattes}" style="width: 98%;" maxlength="300" id="enderecoPlataformaLatters" label="Endere�o para acessar CV na Plataforma Lattes">
						<f:validateLength maximum="300"/>
					</h:inputText>
				</td>
			</tr>
			<tr>
				<td colspan="3">
					<b>Descri��o Pessoal:</b> 
					<span class="info">(Breve descri��o pessoal visualizada por outros usu�rios)</span> <br />
					<h:inputTextarea value="#{perfilDocente.obj.descricao}"	style="width: 98%;" rows="3" />
				</td>
			</tr>
			<tr>
				<td colspan="3">
					<b>Forma��o acad�mica/profissional:</b> 
					<span class="info">(Onde obteve os t�tulos, atua��o profissional, etc)</span> <br/>
					<h:inputTextarea value="#{perfilDocente.obj.formacao}"	style="width: 98%;" rows="3" />
				</td>
			</tr>
			<tr>
				<td colspan="3">
					<b>�reas de Interesse</b> 
					<span class="info">(�reas de interesse de ensino e pesquisa) </span> <br/>
					<h:inputTextarea value="#{perfilDocente.obj.areas}" style="width: 98%;" rows="3" />
				</td>
			</tr>
			<tr>
				<td colspan="3"><b>Assinatura</b> <span class="info">(Utilizada nas mensagens da caixa postal)</span> <br/>
				<h:inputTextarea value="#{perfilDocente.obj.assinatura}" style="width: 98%;" rows="3" />
				</td>
			</tr>	
			</tbody>
			<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton action="#{perfilDocente.cadastrar}" value="Atualizar Perfil" />
					<h:commandButton action="#{perfilDocente.cancelar}" value="Cancelar" onclick="#{confirm}"/>
				</td>
			</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

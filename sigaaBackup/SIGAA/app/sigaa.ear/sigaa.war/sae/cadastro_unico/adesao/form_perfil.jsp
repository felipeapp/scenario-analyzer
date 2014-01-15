<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2> <ufrn:subSistema /> > Informe seu Perfil </h2>

	<div class="descricaoOperacao">
		<p>Caro discente,</p><br/>
		<p>
			Estas informações serão usadas quando um professor desejar buscar alunos de acordo com um perfil na base de dados do SIGAA.
			É muito importante para você descrever aqui todas as suas habilidades e áreas de interesse, pois assim aumentará a chance de um professor te selecionar. 
		</p>
	</div>

	<h:form>
		<table class="formulario" style="width: 75%">
		<caption> Dados do perfil </caption>
		<tr>
			<td style="font-weight:bolder;">
				Descrição Pessoal:<span class="required" />
			</td>
		</tr>
		<tr>
			<td align="center">
				<t:inputTextarea id="descricaoPessoal" value="#{adesaoCadastroUnico.perfil.descricao}" rows="5" style="width: 95%"/>
			</td>
		</tr>
		<tr>
			<td style="font-weight:bolder;">
				Áreas de Interesse:<span class="required" />
			</td>
		</tr>
		<tr>
			<td align="center"><t:inputTextarea id="areasInteresse" value="#{adesaoCadastroUnico.perfil.areas}"style="width: 95%"/></td>
		</tr>
		<tr>
			<td><b>Currículo Lattes:</b></td>
		</tr>
		<tr>
			<td align="center">
				<t:inputText id="curriculoLattes" value="#{adesaoCadastroUnico.perfil.enderecoLattes}"style="width: 96%"/>
			</td>
		</tr>		
		<tfoot>
		<tr>
			<td>
				<h:commandButton action="#{adesaoCadastroUnico.gravarPerfil}" value="Gravar Perfil"/>
				<h:commandButton action="#{adesaoCadastroUnico.cancelar}" value="Cancelar" immediate="true" onclick="#{confirm}"/>
			</td>
		</tr>
		</tfoot>
 		</table>
 		<br/>
 		<div align="center">
			<span class="required" style="font-size: small">&nbsp;</span>
			Campos de preenchimento obrigatório.
		</div>
 		
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
<h2 class="title"><ufrn:subSistema /> > Cadastrar Produ��o Intelectual</h2>
<c:set var="dirBase" value="/prodocente/producao/" scope="session"/>

<h:form>
<table class="listagem">
	<tr>
		<td class="subFormulario" colspan="2">
		Publica��es
		</td>
	</tr>
	<tr>
		<td colspan="2">

			<table style="width:100%">
				<tr>
					<td align="center" width="16%">
						<h:commandButton style="border: 0;" image="/img/prodocente/Artigo.gif" action="#{artigo.preCadastrar}"/><br />
						Artigo, Peri�dicos, Jornais e Similares </td>
					<td align="center" width="16%">
						<h:commandButton style="border: 0;" image="/img/prodocente/Capitulos.gif" action="#{capitulo.preCadastrar}"/><br />
						Cap�tulo de Livros </td>
					<td align="center" width="16%">
						<h:commandButton style="border: 0;" image="/img/prodocente/Livro.gif" action="#{livro.preCadastrar}" /><br />
						Livros </td>
					<td align="center" width="16%">
						<h:commandButton style="border: 0;" image="/img/prodocente/publicacoesEmEvento.gif" action="#{publicacaoEvento.preCadastrar}" /><br />
						Participa��o em Eventos </td>
					<td align="center" width="16%">
						<h:commandButton style="border: 0;" image="/img/prodocente/TextoDidaticoDiscussao.gif" action="#{textoDidatico.preCadastrar}" /><br />
						Textos Did�ticos e Discuss�o </td>
				</tr>
			</table>


		</td>
	</tr>

	<tr>
		<td class="subFormulario" colspan="2">
		Art�sticas, Liter�ria e Visual
		</td>
	</tr>
	<tr>
		<td colspan="2">

			<table width="100%">
				<tr>
					<td align="center" width="25%">
					<h:commandButton style="border: 0;" image="/img/prodocente/AudioVisuais.gif" action="#{audioVisual.preCadastrar}" /><br />
					AudioVisuais </td>
					<td align="center" width="25%">
					<h:commandButton style="border: 0;" image="/img/prodocente/ExposicaoApresentacao.gif" action="#{exposicao.preCadastrar}" /><br />
					Exposi��o ou Apresenta��o Art�sticas </td>
					<td align="center" width="25%">
					<h:commandButton style="border: 0;" image="/img/prodocente/Montagem.gif" action="#{montagem.preCadastrar}" /><br />
					Montagens </td>
					<td align="center" width="25%">
					<h:commandButton style="border: 0;" image="/img/prodocente/ProgramacaoVisual.gif" action="#{programacao.preCadastrar}" /><br />
					Programa��o Visual </td>
				</tr>
			</table>


		</td>
	</tr>

	<tr>
		<td class="subFormulario">
		Tecnol�gicas
		</td>
		<td width="50%" class="subFormulario" style="border-left: 20px solid #FFF;">
		Bancas
		</td>
	</tr>
	<tr>
		<td>

			<table width="100%">
				<tr>
					<td align="center" width="50%">
					<h:commandButton style="border: 0;" image="/img/prodocente/Maquete.gif" action="#{maquetePrototipoOutro.preCadastrar}" /><br />
					Maquetes, Prot�tipos, Softwares e Outros </td>
					<td align="center" width="50%">
					<h:commandButton style="border: 0;" image="/img/prodocente/Patente.gif" action="#{patente.preCadastrar}" /><br />
					Patentes </td>
				</tr>
			</table>
		</td>

		<td>

			<table width="100%">
				<tr>
					<td align="center" width="50%">
					<h:commandButton style="border: 0;" image="/img/prodocente/banca_curso.gif" action="#{banca.formCadastrarCurso}" /><br />
					Trabalhos de Conclus�o </td>
					<td align="center" width="50%">
					<h:commandButton style="border: 0;" image="/img/prodocente/banca_concurso.gif" action="#{banca.formCadastrarConcurso}" /><br />
					Comiss�es Julgadoras </td>
				</tr>
			</table>
		</td>

	</tr>

	<tr>
		<td class="subFormulario" colspan="2">
		Outras
		</td>
	</tr>

	<tr>
		<td colspan="2">

			<table>
				<tr>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/premioRecebido.gif" action="#{premioRecebido.preCadastrar}" /><br />
						Pr�mio Recebido</td>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/BolsaObtida.gif" action="#{bolsaObtida.preCadastrar}" /><br />
						Bolsas Obtidas </td>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/VisitaCientifica.gif" action="#{visitaCientifica.preCadastrar}" /><br />
						Visitas Cient�ficas </td>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/organizacaoEventos.gif" action="#{participacaoComissaoOrgEventos.preCadastrar}" /><br />
						Organiza��o de Eventos, Consultorias, Edi��o e Revis�o de Per�odicos </td>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/participacaoSociedadeCientificaCutural.gif" action="#{participacaoSociedade.preCadastrar}" /><br />
						Participa��o em Sociedades Cient�ficas e Culturais </td>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/colegiadosComissoes.gif" action="#{participacaoColegiadoComissao.preCadastrar}" /><br />
						Participa��o em Colegiados e Comiss�es </td>
				</tr>
			</table>
		</td>
	</tr>

</table>

</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
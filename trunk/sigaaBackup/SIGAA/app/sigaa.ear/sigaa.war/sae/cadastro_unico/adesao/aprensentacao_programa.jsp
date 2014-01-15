<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Cadastro Único de Bolsistas > Apresentação</h2>

<f:view>

<div class="descricaoOperacao">
	<p>
		<center><strong>Programa de Bolsas de Assistência Estudantil</strong></center>
	</p>		
	
	<br/>
	
	<p>
		O programa de bolsa de assistência estudantil é concedido a alunos de cursos regulares, 
		com prioridade para aqueles que se enquadrarem na condição de aluno sócio-economicamente carente.
	</p>
	
	<br/>
	
	<p>
		Entendem-se como cursos regulares aqueles oferecidos por qualquer unidade de ensino vinculada à ${ configSistema['siglaInstituicao'] }, 
		nos níveis médio, técnico profissionalizante ou equivalentes, graduação (presencial ou a distância) e pós-graduação strictu sensu.
	</p>

	<br/>

	<p>
		<strong>Mais detalhes podem ser encontrados na Resolução no <a href="http://www.sigrh.ufrn.br/sigrh/downloadArquivo?idArquivo=78385&key=1487be07add014640fa59dd488710b77" targer="_blank">169/2008-CONSEPE.</a></strong>
	</p>
	<br />
	<br />
	<p>
		<center><strong>Questionário Sócio-Econômico</strong></center>
	</p>		
	
	<br/>

	<p>
		O questionário é uma das formas de avaliação para determinar a condição sócio econômica do aluno. 
		Essa etapa é obrigatória a todos os discentes que desejam participar do programa de bolsa.	
	</p>

	<br/>

	<p>
		A veracidade dos dados informados no cadastro é de sua responsabilidade. 
		Constatando-se que os dados informados são falsos, você poderá sofrer medidas administrativas cabíveis que incluem a perda da bolsa.
	</p>				
</div>
	
	<h:messages showDetail="true"></h:messages>
	
	<h:form id="form">
		<table width="100%">
			<tbody align="center">
				<tr>
					<td>
						<h:selectBooleanCheckbox id="checkConcorda" value="#{adesaoCadastroUnico.termoConcordancia}" /> 
						<label for="form:checkConcorda">
							Eu li e concordo os termos acima citados.
						</label>
					</td>
				</tr>
				<tr>
					<td>
						<h:commandButton value="Continuar >>" action="#{adesaoCadastroUnico.iniciarPerfil}" />
					</td>
				</tr>
			</tbody>
		</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
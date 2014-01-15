<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style type="text/css">
ul.descricaoOperacao li{
   list-style-type: square;
} 
</style>

<h2>  <ufrn:subSistema /> &gt; Visualizar Pap�is do M�dulo de Bibliotecas do ${configSistema['siglaSigaa']}</h2>

	<div class="descricaoOperacao"> 
     	<p> Nesta p�gina � poss�vel visualizar os pap�is do sistema de bibliotecas com suas respectivas opera��es. </p>
     	<p> O usu�rio que possuir um determinado papel no sistema poder� realizar as opera��es que o papel possui. </p>
     	<br/>
     	<p> <strong>Observa��o 1:</strong> As atribui��es dos papeis no sistema s�o de compet�ncia dos GESTORES DE PERMISS�O e s�o realizadas 
     	no <strong>${configSistema['siglaSigadmin']}</strong>. </p>
     	<br/>
     	<p> <strong>Observa��o 2:</strong> Para usu�rios que necessitam realizar uma certa opera��o no sistema apenas durante um per�odo limitado de tempo, � 
     	poss�vel atribuir papeis tempor�rios. Neste caso, depois da data determinada, o usu�rio perder� automaticamente o papel. </p>
	</div>

	<table class="listagem" style="width: 100%;">		
		
		<caption> Pap�is e suas Opera��es no Sistema </caption>

		<thead>
			<th style="text-align: left; width: 40%;">Papel</th>
			<th style="text-align: left; width: 60%;">Opera��es</th>
		</thead>

		<tr class="linhaPar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_ADMINISTRADOR_GERAL </strong> <br/>
				<em> Realiza todas as opera��es do sistema de biblioteca</em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao"> <li> TODAS AS OPERA��ES DO SISTEMA </li> </ul> <br/>
			</td>
		</tr>

		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong>BIBLIOTECA_ADMINISTRADOR_LOCAL </strong> <br/>
				<em> Realiza algumas configura��es e opera��es espec�fica para uma biblioteca do sistema.</em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Lista e Alterar configura��es da sua biblioteca <i>[Aba Cadastros]</i> </li> <br/>
					<li> Cadastrar e Remover uma Interrup��o para SUA biblioteca <i>[Aba Circula��o]</i> </li> <br/>
					<li> Gerenciar os Invent�rios dos Acervo para a SUA biblioteca <i>[Aba Cadastros]</i> </li> <br/>
				</ul>
			</td>
		</tr>
		
		<tr class="linhaPar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO </strong> <br/>
				<em> Realiza todas as opera��es do setor de processos t�cnicos </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao"> 
					<li> Todas as Opera��es do Setor Processos T�cnicos </li>  <br/>
					<li> Cadastrar, Alterar e Remover um Novo Campo MARC Local <i>[Aba Cadastros]</i> </li>  <br/>
					<li> Cadastrar, Alterar e Remover uma Nova Planilha Bibliogr�fica ou de Autoridades <i>[Aba Cadastros]</i> </li>  <br/>
					<li> Registrar, Alterar e Remover Chegada de Fasc�culos (Fasc�culos de Doa��o) <i>[Aba Aquisi��es]</i> </li>  <br/>
					<li> Atender uma solicita��o de Normaliza��o e Cataloga��o na Fonte <i>[Aba Informa��o e Refer�ncia]</i> </li>  <br/>
				</ul>
			</td>
		</tr>
		
		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS </strong> <br/>
				<em> Papel que d� permiss�o aos usu�rio de inclu�rem e alterar as informa��es de materiais do acervo do sistema. Realizam
				praticamente todas as opera��es do setor de processos t�cnicos, menos alterar os dados MARC das cataloga��es. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Incluir exemplares tombados  </li>  <br/>
					<li> Incluir Fasc�culos  </li>  <br/>
					<li> Alterar as informa��es de Exemplares e Fasc�culos  </li>  <br/>
					<li> Alterar dados de V�rios Materiais ao mesmo Tempo.  </li>  <br/>
					<li> Incluir Notas de Circula��o  </li>  <br/>
					<li> Exportar um T�tulo  </li>  <br/>
					<li> Exportar um Autoridade </li>  <br/>
					<li> Imprimir Etiquetas </li>  <br/>
					<li> Baixar, Remover, Desfazer Baixa e Substituir Exemplares e Fasc�culos </li>  <br/>
					<li> Pesquisas no Acervo </li>  <br/>
					<li> Pesquisas na base de Autoridades </li>  <br/>
				</ul>
			</td>
		</tr>
		
		<tr class="linhaPar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_CATALOGACAO </strong> <br/>
				<em> Realiza as tarefas dentro de processos t�cnicos que n�o alterem os dados do acervo. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Exportar T�tulo  </li>  <br/>
					<li> Exportar Autoridade </li>  <br/>
					<li> Impress�o de Etiquetas </li>  <br/>
					<li> Pesquisas no Acervo </li>  <br/>
					<li> Pesquisas na base de Autoridades </li>  <br/>
				</ul>
			</td>
		</tr>


		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_CATALOGACAO_SEM_TOMBAMENTO </strong> <br/>
				<em> Permite incluir exemplares n�o tombados no acervo. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Incluir Exemplares n�o tombados no acervo  </li>  <br/>
				</ul>
			</td>
		</tr>



   
   		<tr class="linhaPar">
			<th style="text-align: left;"> <strong>BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO </strong> <br/>
				<em> Realiza todas as opera��es do setor de circula��o. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Todas as Opera��es do Setor de Circula��o </li>  <br/>
					<li> Realizar Empr�stimos, Renova��oes e Devolu��es <i>[M�dulo de Circula��o Desktop]</i> </li>  <br/>
					<li> Autorizar um Usu�rio Desfazer uma Opera��es <i>[M�dulo de Circula��o Desktop]</i> </li>  <br/>
				</ul>
			</td>
		</tr>
		
		
		
		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_CIRCULACAO </strong> <br/>
				<em> Realiza as tarefas dentro de processos t�cnicos que n�o alterem os dados do acervo. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Realizar Empr�stimos, Renova��oes e Devolu��es <i>[M�dulo de Circula��o Desktop]</i> </li>  <br/>
					<li> Bloquear e Desbloquear um Usu�rios </li> <br/>
					<li> Comunicar Material Perdido  </li> <br/>
					<li> Consultar os Materiais que est�o Emprestados para um Usu�rio  </li> <br/>
					<li> Consultar o Hist�rico de Empr�stimos de um Usu�rio  </li> <br/>
					<li> Consultar o Hist�rico de Empr�stimos de um Material  </li> <br/>
					<li> Incluir e Remover Notas de Circula��oa  </li> <br/>
					<li> Confirmar Pagamento de Multas Manualmente  </li> <br/>
					<li> Imprimir e Reimprimir GRUs para pagamento de Multas   </li> <br/>
					<li> Visualizar Reservas de um Usu�rio.   </li> <br/>
					<li> Solicitar Reservas para um Usu�rio.   </li> <br/>
					<li> Visualizar Reservas de um T�tulo do acervo. </li> <br/>
				</ul>
			</td>
		</tr>
		
		<tr class="linhaPar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_CIRCULACAO_CHECKOUT  </strong> <br/>
				<em> Realiza exclusivamente a opera��o de "ckeckout" no m�dulo de circula��o </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Realizar o Checkout  <i>[M�dulo de Circula��o Desktop]</i> </li>  <br/>
				</ul>
			</td>
		</tr>
   
   		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_EMITE_DECLARACAO_QUITACAO  </strong> <br/>
				<em> Permite a um usu�rio que n�o tenha nenhum papel de circula��o verificar a situa��o de usu�rios da biblioteca 
				  e emitir o comprovante de quita��o. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Verificar Situa��o dos Usu�rios e Emitir Declara��o de Quita��o. </li>  <br/>
				</ul>
			</td>
		</tr>
   
   		<tr class="linhaPar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO  </strong> <br/>
				<em> Permite a realiza��o de todas as opera��es dentro do setor de aquisi��o </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Todas as Opera��es do Setor de Aquisi��o </li>  <br/>
				</ul>
			</td>
		</tr>
		
		
		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_AQUISICAO  </strong> <br/>
				<em> Realiza opera��es menos cr�ticas de aquisi��o. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Registrar Chegada de Fasc�culos  </li>  <br/>
					<li> Alterar / Remover Fasc�culos Registrados  </li>  <br/>
					<li> Cadastrar, Alterar e Remover Periodicidades  </li>  <br/>
				</ul>
			</td>
		</tr>
   
   		<tr class="linhaPar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO  </strong> <br/>
				<em> Realiza todas as opera��es de informa��o e refer�ncia. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Todas as Opera��es do Setor de Informa��o e Refer�ncia </li>  <br/>
				</ul>
			</td>
		</tr>
		
		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_INFO_E_REF  </strong> <br/>
				<em> Realiza opera��es menos cr�ticas de Informa��o e Refer�ncia. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Realizar, Renovar e Devolver Empr�stimos Institucionais</li>  <br/>
				</ul>
			</td>
		</tr>
  
  
  		<tr class="linhaPar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO  </strong> <br/>
				<em> Permite ao usu�rio que o possua visualizar  os relat�rios do sistema. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Emitir Relat�rios de Processos T�cnicos e Circula��o   </li>  <br/>
					
				</ul>
			</td>
		</tr>
		
		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_GESTOR_BDTD </strong> <br/>
				<em> Realiza as opera��es relacionadas a BDTD no sistema. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Catalogar uma Teses ou Disserta��es   </li>  <br/>
					<li> Consultar Teses ou Disserta��es Pendentes de Publica��o na BDTD    </li>  <br/>
					<li> Consultar Solicita��es (TEDE)   </li>  <br/>
					<li> Consultar Bancas    </li>  <br/>
				</ul>
			</td>
		</tr>
		
		<tfoot>
			<tr>
				<td colspan="2" style="height: 20px;">
				</td>
			</tr>
		</tfoot>
   
	</table>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>